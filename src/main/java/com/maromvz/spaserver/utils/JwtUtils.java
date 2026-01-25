package com.maromvz.spaserver.utils;

import com.maromvz.spaserver.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    private final int jwtExpirationMs = 15 * 60 * 1000;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private SecretKey getKey() {
        log.info("Secret key: " + jwtSecret);

        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateTokenFromEmail(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getKey())
                .compact();
    }

    public String generateJwtToken(Authentication auth) {
        User user = (User) auth.getPrincipal();

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getKey())
                .compact();
    }

    public boolean verifyJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
