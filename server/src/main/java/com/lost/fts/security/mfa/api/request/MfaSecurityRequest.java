package com.lost.fts.security.mfa.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MfaSecurityRequest(

        @NotBlank
        String password,

        @NotBlank
        @Pattern(regexp = "\\d{6}")
        String code
) {
}
