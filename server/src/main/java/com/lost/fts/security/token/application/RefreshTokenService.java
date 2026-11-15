package com.lost.fts.security.token.application;

public interface RefreshTokenService {

    String createRefreshToken(Long userId);

    RefreshTokenPair rotateRefreshToken(String rawToken);

    void revokeRefreshToken(String rawToken);

    void revokeAllUserTokens(Long userId);
}