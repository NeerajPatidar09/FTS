package com.lost.fts.security.mfa.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MfaLoginRequest(

        @NotBlank
        String challengeToken,

        @NotBlank
        @Pattern(regexp = "\\d{6}")
        String code
) {
}