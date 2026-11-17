package com.lost.fts.security.mfa.api.response;

public record MfaVerificationResult(boolean verified, Long userId) {
}