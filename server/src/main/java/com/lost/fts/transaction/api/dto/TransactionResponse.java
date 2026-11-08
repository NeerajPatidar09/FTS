package com.lost.fts.transaction.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.lost.fts.transaction.infrastructure.persistence.TransactionChannel;
import com.lost.fts.transaction.infrastructure.persistence.TransactionEntity;
import com.lost.fts.transaction.infrastructure.persistence.TransactionStatus;
import com.lost.fts.transaction.infrastructure.persistence.TransactionType;

public record TransactionResponse(
        Long id,
        String transactionRef,
        String sourceSystem,
        String externalTransactionRef,
        String sourceAccountRef,
        String destinationAccountRef,
        TransactionType transactionType,
        TransactionChannel channel,
        TransactionStatus status,
        BigDecimal amount,
        String currency,
        Instant occurredAt,
        Instant createdAt) {
    public static TransactionResponse from(TransactionEntity entity) {
        return new TransactionResponse(
                entity.getId(),
                entity.getTransactionRef(),
                entity.getSourceSystem(),
                entity.getExternalTransactionRef(),
                entity.getSourceAccount() == null
                        ? null
                        : entity.getSourceAccount().getAccountRef(),
                entity.getDestinationAccount() == null
                        ? null
                        : entity.getDestinationAccount().getAccountRef(),
                entity.getTransactionType(),
                entity.getChannel(),
                entity.getStatus(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getOccurredAt(),
                entity.getCreatedAt());
    }
}
