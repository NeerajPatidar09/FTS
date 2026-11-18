package com.lost.fts.security.password.api.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(@NotBlank String currentPassword,@NotBlank String newPassword) {
    
}