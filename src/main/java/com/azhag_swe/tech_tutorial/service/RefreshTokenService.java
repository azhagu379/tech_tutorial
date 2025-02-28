package com.azhag_swe.tech_tutorial.service;

import com.azhag_swe.tech_tutorial.config.DataInitializer;
import com.azhag_swe.tech_tutorial.config.JwtConfig;
import com.azhag_swe.tech_tutorial.model.entity.RefreshToken;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.repository.RefreshTokenRepository;
import com.azhag_swe.tech_tutorial.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    

  @Transactional
    public RefreshToken createOrReplaceRefreshToken(String username) {
        // Retrieve the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // Check if a refresh token already exists for the user
        refreshTokenRepository.findByUser(user).ifPresent(existingToken -> {
            log.info("Deleting existing refresh token for user: {}", username);
            refreshTokenRepository.delete(existingToken);
        });

        // Create a new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(30, ChronoUnit.DAYS)) // Adjust expiry duration as needed
                .build();

        log.info("Saving new refresh token for user: {}", username);
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
