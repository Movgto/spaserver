package com.maromvz.spaserver.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import org.springframework.boot.web.server.Cookie.SameSite;

import java.util.stream.Stream;

@Slf4j
@Component
public class RefreshTokenUtils {

    @Value("${app.auth.refreshToken.cookieName}")
    private String refreshCookieName;

    @Value("${app.auth.cookieSecure}")
    private boolean isCookieSecure;

    public ResponseCookie generateRefreshCookie(String refreshToken) {
        return ResponseCookie.from(refreshCookieName, refreshToken)
                .path("/auth/refresh")
                .httpOnly(true)
                .secure(isCookieSecure)
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60) // Seven days until expiration.
                .build();
    }

    public String getRefreshTokenFromRequest(HttpServletRequest req) {
        Cookie cookie = WebUtils.getCookie(req, refreshCookieName);

        log.info(cookie.getValue());

        log.info("Cookies in the request:");

        Stream.of(req.getCookies()).forEach(c -> log.info(c.getName() + " = " + c.getValue()));

        return cookie != null ? cookie.getValue() : null;
    }
}
