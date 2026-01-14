package fr.huiitre.tools.api.core.auth;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.api.core.auth.dto.GoogleLoginRequest;
import fr.huiitre.tools.api.core.auth.dto.LoginRequest;
import fr.huiitre.tools.api.core.auth.dto.LoginResponse;
import fr.huiitre.tools.api.core.auth.dto.RegisterRequest;
import fr.huiitre.tools.api.core.auth.dto.RegisterResponse;
import fr.huiitre.tools.application.core.auth.AuthProvider;
import fr.huiitre.tools.application.core.auth.AuthenticateUserWithProviderUseCase;
import fr.huiitre.tools.application.core.auth.AuthenticateWithProviderCommand;
import fr.huiitre.tools.application.core.auth.exception.UserNotFoundException;
import fr.huiitre.tools.application.core.auth.LoginUserCommand;
import fr.huiitre.tools.application.core.auth.LoginUserUseCase;
import fr.huiitre.tools.application.core.auth.RegisterUserAndSendVerificationUseCase;
import fr.huiitre.tools.application.core.auth.RegisterUserCommand;
import fr.huiitre.tools.application.core.auth.RequestPasswordResetUseCase;
import fr.huiitre.tools.application.core.auth.ValidateEmailVerificationUseCase;
import fr.huiitre.tools.application.core.auth.ValidatePasswordResetUseCase;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.domain.core.user.User;
import fr.huiitre.tools.infrastructure.auth.google.GoogleTokenVerifier;
import fr.huiitre.tools.infrastructure.auth.google.GoogleUserPayload;
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
    private final LoginUserUseCase loginUserUseCase;
    private final UserRepository userRepository;
    private final AuthenticateUserWithProviderUseCase authenticateUserWithProviderUseCase;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final ValidateEmailVerificationUseCase validateEmailVerificationUseCase;
    private final RegisterUserAndSendVerificationUseCase registerUserAndSendVerificationUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ValidatePasswordResetUseCase validatePasswordResetUseCase;

    public AuthController(
        JwtProvider jwtProvider,
        SecurityCookieProperties cookieProperties,
        RegisterUserAndSendVerificationUseCase registerUserAndSendVerificationUseCase,
        LoginUserUseCase loginUserUseCase,
        UserRepository userRepository,
        AuthenticateUserWithProviderUseCase authenticateUserWithProviderUseCase,
        GoogleTokenVerifier googleTokenVerifier,
        ValidateEmailVerificationUseCase validateEmailVerificationUseCase,
        RequestPasswordResetUseCase requestPasswordResetUseCase,
        ValidatePasswordResetUseCase validatePasswordResetUseCase
    ) {
        this.jwtProvider = jwtProvider;
        this.cookieProperties = cookieProperties;
        this.registerUserAndSendVerificationUseCase = registerUserAndSendVerificationUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.userRepository = userRepository;
        this.authenticateUserWithProviderUseCase = authenticateUserWithProviderUseCase;
        this.googleTokenVerifier = googleTokenVerifier;
        this.validateEmailVerificationUseCase = validateEmailVerificationUseCase;
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.validatePasswordResetUseCase = validatePasswordResetUseCase;
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
     * LOGIN / REGISTER GOOGLE
     * ===============================
     */
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletResponse response
    ) {

        // 1. Vérifier le token Google (INFRA)
        GoogleUserPayload payload = googleTokenVerifier.verify(request.getIdToken());

        // 2. Construire la commande métier
        AuthenticateWithProviderCommand command =
            new AuthenticateWithProviderCommand(
                AuthProvider.GOOGLE,
                payload.getProviderUserId(),
                payload.getEmail(),
                payload.getName()
            );

        // 3. Authentifier (login OU register implicite)
        User user = authenticateUserWithProviderUseCase.execute(command);

        // 4. Générer les tokens (IDENTIQUE AU LOGIN CLASSIQUE)
        String accessToken = jwtProvider.generateAccessToken(
            user.getId().toString(),
            buildAccessClaims(user)
        );

        String refreshToken = jwtProvider.generateRefreshToken(
            user.getId().toString()
        );

        // 5. Cookie refresh token
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(cookieProperties.isSecure());
        refreshCookie.setPath("/api/v3/auth");
        refreshCookie.setMaxAge(7 * 24 * 3600);
        refreshCookie.setAttribute("SameSite", "Strict");

        response.addCookie(refreshCookie);

        // 6. Retour access token
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

        RegisterUserCommand command =
            RegisterUserCommand.password(
                request.getEmail(),
                request.getName(),
                request.getPassword()
            );

        registerUserAndSendVerificationUseCase.execute(command);

        return ResponseEntity.ok(
                new RegisterResponse("EMAIL_VERIFICATION_REQUIRED", "Un email de confirmation vous a été envoyé. Veuillez valider votre adresse pour activer votre compte.")
        );
    }

    private Map<String, Object> buildAccessClaims(User user) {
        return Map.of(
                "tokenType", "ACCESS",
                "userType", user.getUserType().name(),
                "isActive", user.isActive());
    }

    /*
     * ===============================
     * VALIDATION EMAIL
     * ===============================
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
        @RequestParam("token") String token
    ) {
        validateEmailVerificationUseCase.execute(token);
        return ResponseEntity.ok(
            Map.of(
                "status", "EMAIL_VERIFIED",
                "message", "Adresse email vérifiée avec succès"
            )
        );
    }

    /*
     * ===============================
     * RECOVERY PASSWORD
     * ===============================
     */
    @PostMapping("/password/reset-request")
    public ResponseEntity<?> requestPasswordReset(
        @RequestBody PasswordResetRequestDto request
    ) {
        requestPasswordResetUseCase.execute(request.email());
        return ResponseEntity.ok(
            Map.of(
                "status", "RESET_REQUESTED",
                "message", "Si un compte correspondant existe, un email a été envoyé"
            )
        );
    }
    public record PasswordResetRequestDto(String email) {}

    /*
     * ===============================
     * CHANGE PASSWORD
     * ===============================
     */
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(
        @RequestBody PasswordResetDto request
    ) {
        validatePasswordResetUseCase.execute(request.token(), request.password());
        return ResponseEntity.ok(
            Map.of(
                "status", "PASSWORD_RESET",
                "message", "Mot de passe modifié avec succès"
            )
        );
    }
    public record PasswordResetDto(String token, String password) {}
}
