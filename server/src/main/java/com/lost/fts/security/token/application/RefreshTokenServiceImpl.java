package com.lost.fts.security.token.application;

import com.lost.fts.security.jwt.JwtService;
import com.lost.fts.security.userdetails.CustomUserDetailsService;
import com.lost.fts.security.token.infrastructure.persistence.*;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshExpiration;

    @Value("${app.jwt.access-token-expiration}")
    private long accessExpiration;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public String createRefreshToken(Long userId) {

        String rawToken = generateSecureToken();

        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(
                Instant.now().plusMillis(refreshExpiration)
        );

        refreshTokenRepository.save(token);

        return rawToken;
    }

    @Override
    @Transactional
    public RefreshTokenPair rotateRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        String oldHash = hashToken(rawToken);

        RefreshToken oldToken = refreshTokenRepository
                .findByTokenHashForUpdate(oldHash)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid refresh token")
                );

        if (oldToken.isRevoked()) {
            // A revoked token being reused can indicate token theft.
            refreshTokenRepository.revokeAllByUserId(oldToken.getUserId());
            throw new IllegalArgumentException("Refresh token reuse detected");
        }

        if (oldToken.isExpired()) {
            oldToken.revoke(null);
            throw new IllegalArgumentException("Refresh token expired");
        }

        User user = userRepository.findById(oldToken.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        // Adapt this check to your actual User entity status fields.
        if (!user.isEnabled()) {
            oldToken.revoke(null);
            throw new IllegalArgumentException("User account is disabled");
        }

        String newRawToken = generateSecureToken();
        String newHash = hashToken(newRawToken);

        RefreshToken newToken = new RefreshToken();
        newToken.setUserId(user.getId());
        newToken.setTokenHash(newHash);
        newToken.setExpiresAt(
                Instant.now().plusMillis(refreshExpiration)
        );

        refreshTokenRepository.save(newToken);

        oldToken.revoke(newHash);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getUsername());

        String newAccessToken = jwtService.generateToken(userDetails);

        return new RefreshTokenPair(
                newAccessToken,
                newRawToken,
                accessExpiration / 1000
        );
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        refreshTokenRepository.findByTokenHashForUpdate(hashToken(rawToken))
                .ifPresent(token -> {
                    if (!token.isRevoked()) {
                        token.revoke(null);
                    }
                });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    private String generateSecureToken() {

        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return java.util.HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to hash refresh token", ex
            );
        }
    }
}