package com.lost.fts.customer.api.response;

import java.time.Instant;

import com.lost.fts.customer.infrastructure.persistence.CustomerStatus;

public record CustomerResponse(
    Long id,
    String customerRef,
    String firstName,
    String lastName,
    String fullName,
    // String maskedPan,
    // String maskedPhone,
    String maskedEmail,
    CustomerStatus status,
    Instant createdAt,
    Instant updatedAt
) {}
