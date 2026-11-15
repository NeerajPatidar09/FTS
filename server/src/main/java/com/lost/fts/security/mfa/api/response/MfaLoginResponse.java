package com.lost.fts.security.mfa.api.response;

public record MfaLoginResponse(

        boolean mfaRequired,

        String challengeToken,

        String accessToken,

        String tokenType,

        Long expiresIn,

        String message

) {
}