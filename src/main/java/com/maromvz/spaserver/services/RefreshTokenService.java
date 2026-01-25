package com.maromvz.spaserver.services;

import com.maromvz.spaserver.entities.RefreshToken;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.repo.RefreshTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.auth.refreshToken.duration}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken oldRefreshToken = refreshTokenRepo.findByUserId(user.getId()).orElse(null);

        if (oldRefreshToken != null) {
            refreshTokenRepo.delete(oldRefreshToken);
        }

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken verifyTokenExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepo.delete(refreshToken);

            throw new RuntimeException("The refresh token has expired");
        }

        return refreshToken;
    }
}
