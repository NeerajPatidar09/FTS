package com.lost.fts.security.token.application;

import com.lost.fts.security.session.infrastructure.persistence.UserSession;

import jakarta.servlet.http.HttpServletRequest;

public interface RefreshTokenService {

    String createRefreshToken(Long userId, UserSession session);

    RefreshTokenPair rotateRefreshToken(String rawToken, HttpServletRequest httpRequest);

    void revokeRefreshToken(String rawToken);

    void revokeAllUserTokens(Long userId);
}