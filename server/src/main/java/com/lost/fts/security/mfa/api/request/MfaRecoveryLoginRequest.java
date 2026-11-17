package com.lost.fts.security.mfa.api.request;

import jakarta.validation.constraints.NotBlank;

public record MfaRecoveryLoginRequest(@NotBlank String challengeToken, @NotBlank String recoveryCode) {
}