package com.lost.fts.transaction.application;

import java.math.BigDecimal;
import java.time.Instant;

import com.lost.fts.transaction.infrastructure.persistence.TransactionChannel;
import com.lost.fts.transaction.infrastructure.persistence.TransactionType;

public record TransactionReceivedEvent(
        String eventId,
        String eventType,
        Long transactionId,
        String transactionRef,
        Long sourceAccountId,
        Long destinationAccountId,
        BigDecimal amount,
        String currency,
        TransactionType transactionType,
        TransactionChannel channel,
        Instant occurredAt) {
}
