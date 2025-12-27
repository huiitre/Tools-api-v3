package fr.huiitre.tools.api.core.auth;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.api.core.auth.dto.LoginRequest;
import fr.huiitre.tools.api.core.auth.dto.LoginResponse;
import fr.huiitre.tools.api.core.auth.dto.RegisterRequest;
import fr.huiitre.tools.api.core.auth.dto.RegisterResponse;
import fr.huiitre.tools.application.core.auth.AuthProvider;
import fr.huiitre.tools.application.core.auth.exception.UserNotFoundException;
import fr.huiitre.tools.application.core.auth.login.command.LoginUserCommand;
import fr.huiitre.tools.application.core.auth.login.usecase.LoginUserUseCase;
import fr.huiitre.tools.application.core.auth.register.command.RegisterUserUseCase;
import fr.huiitre.tools.application.core.auth.register.usecase.RegisterUserCommand;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.domain.core.user.User;
import fr.huiitre.tools.infrastructure.security.JwtProvider;
import fr.huiitre.tools.infrastructure.security.SecurityCookieProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Tag(name = "Core - Auth")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtProvider jwtProvider;
    private final SecurityCookieProperties cookieProperties;
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final UserRepository userRepository;

    public AuthController(
        JwtProvider jwtProvider,
        SecurityCookieProperties cookieProperties,
        RegisterUserUseCase registerUserUseCase,
        LoginUserUseCase loginUserUseCase,
        UserRepository userRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.cookieProperties = cookieProperties;
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.userRepository = userRepository;
    }

    /*
     * ===============================
     * LOGIN (TEMPORAIRE / TECHNIQUE)
     * ===============================
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        // * Récupère les crédentials */
        LoginUserCommand command = new LoginUserCommand(request.getEmail(), request.getPassword());

        // * on exécute la logique de login */
        User user = loginUserUseCase.execute(command);

        // * génère le token */
        String accessToken = jwtProvider.generateAccessToken(
                user.getId().toString(),
                buildAccessClaims(user));

        String refreshToken = jwtProvider.generateRefreshToken(user.getId().toString());

        // * Cookie HttpOnly pour le refresh token */
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(cookieProperties.isSecure()); // HTTPS only (OK derrière proxy)
        refreshCookie.setPath("/api/v3/auth"); // limité à l’auth
        refreshCookie.setMaxAge(7 * 24 * 3600); // 7 jours
        refreshCookie.setAttribute("SameSite", "Strict");

        response.addCookie(refreshCookie);

        // Access token retourné au front
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    /*
     * ===============================
     * REFRESH TOKEN
     * ===============================
     */
    @PostMapping("/refresh")
    public Map<String, String> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        try {

            Cookie[] cookies = request.getCookies();

            if (cookies == null) {
                logger.warn(
                        "AUTH_REFRESH_FAILURE ip={} reason=NO_COOKIE",
                        request.getRemoteAddr());
                throw new UserNotFoundException("Utilisateur introuvable");
            }

            String refreshToken = null;
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }

            if (refreshToken == null) {
                logger.warn(
                        "AUTH_REFRESH_FAILURE ip={} reason=NO_REFRESH_TOKEN",
                        request.getRemoteAddr());
                throw new UserNotFoundException("Utilisateur introuvable");
            }

            // 1. Valider l’ancien refresh token
            Claims claims = jwtProvider.parseToken(refreshToken);
            Long userId = Long.parseLong(claims.getSubject());

            User user = userRepository
                    .findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

            if (!user.isActive()) {
                throw new UserNotFoundException("Utilisateur désactivé");
            }

            // 2. Rotation du refresh token
            String newRefreshToken = jwtProvider.generateRefreshToken(user.getId().toString());

            Cookie newRefreshCookie = new Cookie("refresh_token", newRefreshToken);
            newRefreshCookie.setHttpOnly(true);
            newRefreshCookie.setSecure(cookieProperties.isSecure());
            newRefreshCookie.setPath("/api/v3/auth");
            newRefreshCookie.setMaxAge(7 * 24 * 3600);
            newRefreshCookie.setAttribute("SameSite", "Strict");

            response.addCookie(newRefreshCookie);

            // 3. Génération du nouvel access token
            String newAccessToken = jwtProvider.generateAccessToken(
                    user.getId().toString(),
                    buildAccessClaims(user));

            logger.info(
                    "AUTH_REFRESH_SUCCESS user={} ip={}",
                    user.getId().toString(),
                    request.getRemoteAddr());

            return Map.of("accessToken", newAccessToken);

        } catch (ExpiredJwtException e) {

            logger.warn(
                    "AUTH_REFRESH_FAILURE ip={} reason=REFRESH_EXPIRED",
                    request.getRemoteAddr());
            throw e;

        } catch (JwtException | IllegalArgumentException e) {

            logger.warn(
                    "AUTH_REFRESH_FAILURE ip={} reason=REFRESH_INVALID",
                    request.getRemoteAddr());
            throw e;
        }
    }

    /*
     * ===============================
     * LOGOUT
     * ===============================
     */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie deleteRefreshCookie = new Cookie("refresh_token", "");
        deleteRefreshCookie.setHttpOnly(true);
        deleteRefreshCookie.setSecure(cookieProperties.isSecure());
        deleteRefreshCookie.setPath("/api/v3/auth");
        deleteRefreshCookie.setMaxAge(0);
        deleteRefreshCookie.setAttribute("SameSite", "Strict");

        logger.info(
                "AUTH_LOGOUT ip={} userAgent={}",
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        response.addCookie(deleteRefreshCookie);
    }

    /*
     * ===============================
     * REGISTER
     * ===============================
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        RegisterUserCommand command = new RegisterUserCommand(
                AuthProvider.PASSWORD,
                request.getEmail(),
                request.getPassword(),
                request.getName());

        User user = registerUserUseCase.execute(command);

        return ResponseEntity.ok(
                new RegisterResponse("REGISTER_SUCCESS", "Inscription réussie."));
    }

    private Map<String, Object> buildAccessClaims(User user) {
        return Map.of(
                "tokenType", "ACCESS",
                "userType", user.getUserType().name(),
                "isActive", user.isActive());
    }
}
