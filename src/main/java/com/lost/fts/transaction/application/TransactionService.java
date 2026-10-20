package com.lost.fts.transaction.application;

import com.lost.fts.transaction.api.dto.TransactionCreateRequest;
import com.lost.fts.transaction.api.dto.TransactionCreateResult;
import com.lost.fts.transaction.api.dto.TransactionResponse;

public interface TransactionService {
    TransactionCreateResult ingest(TransactionCreateRequest request);
    TransactionResponse getByTransactionRef(String transactionRef);
}
