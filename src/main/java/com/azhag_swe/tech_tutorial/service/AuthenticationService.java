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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    @Autowired
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationResponse register(RegisterRequest request) {
        // Map roles from String to Role entities
        Set<Role> roles = request.roles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password()); // Consider hashing this password
        user.setRoles(roles);

        userRepository.save(user);

        UserDetails userDetails = (UserDetails) user;
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtService.generateToken((UserDetails) user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IOException("Invalid refresh token");
        }

        String refreshToken = authHeader.substring(7);
        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IOException("User not found"));

            if (jwtService.isTokenValid(refreshToken, (UserDetails) user)) {
                String accessToken = jwtService.generateToken((UserDetails) user);
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new IOException("Invalid refresh token");
    }
}
