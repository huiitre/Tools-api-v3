package fr.huiitre.tools.infrastructure.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieManager {

    private static final String COOKIE_NAME = "refresh_token";
    private static final String COOKIE_PATH = "/api/v3/auth";
    private static final String SAME_SITE = "Strict";

    private final SecurityCookieProperties cookieProperties;

    public RefreshTokenCookieManager(SecurityCookieProperties cookieProperties) {
        this.cookieProperties = cookieProperties;
    }

    public void set(HttpServletResponse response, String refreshToken, int maxAgeSeconds) {
        Cookie cookie = new Cookie(COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieProperties.isSecure());
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setAttribute("SameSite", SAME_SITE);
        response.addCookie(cookie);
    }

    public void clear(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieProperties.isSecure());
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", SAME_SITE);
        response.addCookie(cookie);
    }
}
