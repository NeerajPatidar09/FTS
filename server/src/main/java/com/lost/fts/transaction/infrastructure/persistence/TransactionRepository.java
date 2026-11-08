package com.lost.fts.transaction.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    Optional<TransactionEntity> findByTransactionRef(String transactionRef);

    Optional<TransactionEntity> findBySourceSystemAndExternalTransactionRef(String sourceSystem, String externalTransactionRef);

    List<TransactionEntity> findBySourceAccountIdAndOccurredAtBetween(Long accountId, Instant start, Instant end);

    List<TransactionEntity> findByDestinationAccountIdAndOccurredAtBetween(Long accountId, Instant start, Instant end);

    List<TransactionEntity> findByStatusAndOccurredAtBetween(TransactionStatus status, Instant start, Instant end);
}
