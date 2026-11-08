package com.lost.fts.transaction.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;

import com.lost.fts.transaction.infrastructure.persistence.TransactionChannel;
import com.lost.fts.transaction.infrastructure.persistence.TransactionType;
import jakarta.validation.constraints.*;

public record TransactionCreateRequest(
        @NotBlank @Size(max = 64) String sourceSystem,

        @Size(max = 128) String externalTransactionRef,

        String sourceAccountRef,

        String destinationAccountRef,

        @Size(max = 64) String beneficiaryRef,

        Long customerDeviceId,

        @NotNull TransactionType transactionType,

        @NotNull TransactionChannel channel,

        @NotNull @DecimalMin(value = "0.0001") @Digits(integer = 15, fraction = 4) BigDecimal amount,

        @NotBlank @Pattern(regexp = "^[A-Za-z]{3}$") String currency,

        @Size(max = 128) String counterpartyReferenceToken,

        @Size(max = 100) String counterpartyMasked,

        @NotNull Instant occurredAt) {

    public TransactionCreateRequest {
        if (currency != null) {
            currency = currency.trim().toUpperCase(Locale.ROOT);
        }

        if (sourceSystem != null) {
            sourceSystem = sourceSystem.trim();
        }

        if (externalTransactionRef != null) {
            externalTransactionRef = externalTransactionRef.trim();
        }
    }

}
