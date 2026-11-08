package com.lost.fts.user.api.request;

import jakarta.validation.constraints.*;

public record UserCreateRequest(
    Long employeeId,
    @NotBlank @Size(min = 4, max = 50) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 12, max = 72) String initialPassword) {}