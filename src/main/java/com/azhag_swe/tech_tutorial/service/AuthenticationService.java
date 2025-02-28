package com.azhag_swe.tech_tutorial.service;

import com.azhag_swe.tech_tutorial.dto.request.AuthenticationRequest;
import com.azhag_swe.tech_tutorial.dto.request.RegisterRequest;
import com.azhag_swe.tech_tutorial.dto.response.AuthenticationResponse;
import com.azhag_swe.tech_tutorial.model.entity.RefreshToken;
import com.azhag_swe.tech_tutorial.model.entity.Role;
import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.repository.RoleRepository;
import com.azhag_swe.tech_tutorial.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.username());

        // Map roles from String to Role entities
        Set<Role> roles = request.roles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> {
                            log.error("Role not found: {}", roleName);
                            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName);
                        }))
                .collect(Collectors.toSet());

        if (userRepository.findByUsername(request.username()).isPresent()) {
            log.error("Username {} is already taken", request.username());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password())); // Hash password before saving
        user.setRoles(roles);

        userRepository.save(user);
        log.info("User {} registered successfully", request.username());

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createOrReplaceRefreshToken(user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Authenticating user: {}", request.username());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()));
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.username());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    log.error("User not found: {}", request.username());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createOrReplaceRefreshToken(user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        log.info("Refreshing access token");

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Invalid Authorization header");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Authorization header");
        }

        String refreshToken = authHeader.substring(7);
        String username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            log.error("Invalid refresh token: no username found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid refresh token");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found for refresh token: {}", username);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        if (!jwtService.isTokenValid(refreshToken, user)) {
            log.error("Invalid refresh token for user: {}", username);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        log.info("Access token refreshed for user: {}", username);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
