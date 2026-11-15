package com.lost.fts.security.authentication.api.response;

public record RefreshResponse(String accessToken, String tokenType, long expiresIn) {
}