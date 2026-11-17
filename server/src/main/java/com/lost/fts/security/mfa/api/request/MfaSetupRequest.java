package com.lost.fts.security.mfa.api.request;

import jakarta.validation.constraints.NotBlank;

public record MfaSetupRequest(
        @NotBlank String password
) {
}