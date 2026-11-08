package com.lost.fts.detection.application;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import com.lost.fts.transaction.infrastructure.persistence.TransactionEntity;

public record RuleEvaluationContext(
        TransactionEntity transaction,
        long recentTransactionCount,
        BigDecimal recentTransactionTotal,
        Instant beneficiaryCreatedAt,
        Instant evaluationTime
) {
    public RuleEvaluationContext {
        if (transaction == null || evaluationTime == null) {
            throw new IllegalArgumentException(
                    "Transaction and evaluation time are required"
            );
        }

        if (recentTransactionCount < 0) {
            throw new IllegalArgumentException(
                    "Transaction count cannot be negative"
            );
        }

        if (recentTransactionTotal == null
                || recentTransactionTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Recent transaction total must be non-negative"
            );
        }
    }

    public boolean hasNewBeneficiary(Duration window) {
        if (beneficiaryCreatedAt == null) {
            return false;
        }

        Instant cutoff = evaluationTime.minus(window);

        return !beneficiaryCreatedAt.isBefore(cutoff)
                && !beneficiaryCreatedAt.isAfter(evaluationTime);
    }
}