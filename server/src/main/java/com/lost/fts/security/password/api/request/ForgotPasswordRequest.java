package com.lost.fts.security.password.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
    @NotBlank
    @Email
    String email
) {}