package com.lost.fts.security.authentication.api.response;

public record LoginResult(
        boolean mfaRequired,
        String challengeToken,
        LoginResponse loginResponse,
        String refreshToken) {

    public static LoginResult challenge(String token) {
        return new LoginResult(true, token, null, null);
    }

    public static LoginResult authenticated(
            LoginResponse response,
            String refreshToken) {
        return new LoginResult(
                false, null, response, refreshToken);
    }
}