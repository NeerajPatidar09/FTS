package com.lost.fts.security.mfa.api.response;

public record MfaSetupResponse(

        String secret,

        String provisioningUri,

        String message

) {
}