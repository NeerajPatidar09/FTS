package com.lost.fts.security.mfa.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MfaVerifyRequest(

        @NotBlank(message = "OTP code is required")
        @Pattern(
                regexp = "^[0-9]{6}$",
                message = "OTP must contain exactly 6 digits"
        )
        String code

) {
}