package com.lost.fts.account.api.request;

import java.time.LocalDate;

import com.lost.fts.account.infrastructure.persistence.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountCreateRequest(

    @NotBlank(message = "Account reference is required")
    @Size(max = 64)
    @Pattern(
        regexp = "^[A-Za-z0-9_-]+$",
        message = "Account reference can contain letters, numbers, hyphens and underscores only"
    )
    String accountRef,

    @NotBlank(message = "Customer reference is required")
    String customerRef,

    @NotNull(message = "Account type is required")
    AccountType accountType,

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter uppercase code")
    String currency,

    @NotNull(message = "Opening date is required")
    LocalDate openedOn

) {}
