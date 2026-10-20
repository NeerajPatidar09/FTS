package com.lost.fts.account.api.response;

import java.time.Instant;
import java.time.LocalDate;

import com.lost.fts.account.infrastructure.persistence.AccountStatus;
import com.lost.fts.account.infrastructure.persistence.AccountType;

public record AccountResponse(
    Long id,
    String accountRef,
    String customerRef,
    AccountType accountType,
    AccountStatus status,
    String currency,
    LocalDate openedOn,
    LocalDate closedOn,
    Instant createdAt,
    Instant updatedAt
) {}