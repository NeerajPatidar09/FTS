package com.lost.fts.security.password.api.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank String token,
    @NotBlank String newPassword
) {}