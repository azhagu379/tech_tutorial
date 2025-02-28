package com.azhag_swe.tech_tutorial.service;

import com.azhag_swe.tech_tutorial.config.JwtConfig;
import com.azhag_swe.tech_tutorial.model.entity.RefreshToken;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.repository.RefreshTokenRepository;
import com.azhag_swe.tech_tutorial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(jwtConfig.getRefreshTokenExpiration()))
                .build();
        
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }
}
