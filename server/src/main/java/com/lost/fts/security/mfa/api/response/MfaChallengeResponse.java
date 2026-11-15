package com.lost.fts.security.mfa.api.response;

public record MfaChallengeResponse(
        boolean mfaRequired,
        String challengeToken,
        long expiresIn,
        String message
) {
}