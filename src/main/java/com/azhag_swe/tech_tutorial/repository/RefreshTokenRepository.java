package com.azhag_swe.tech_tutorial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azhag_swe.tech_tutorial.model.entity.RefreshToken;
import com.azhag_swe.tech_tutorial.model.entity.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}