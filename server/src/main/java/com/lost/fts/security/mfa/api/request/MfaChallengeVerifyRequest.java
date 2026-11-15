package com.lost.fts.security.mfa.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MfaChallengeVerifyRequest(

        @NotBlank(message = "Challenge token is required")
        @Size(max = 512)
        String challengeToken,

        @NotBlank(message = "OTP code is required")
        @Pattern(
                regexp = "^[0-9]{6}$",
                message = "OTP must contain exactly 6 digits"
        )
        String code

) {
}