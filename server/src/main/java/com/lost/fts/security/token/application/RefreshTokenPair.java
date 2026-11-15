package com.lost.fts.security.token.application;

public record RefreshTokenPair(
        String accessToken,
        String refreshToken,
        long expiresIn) {
}