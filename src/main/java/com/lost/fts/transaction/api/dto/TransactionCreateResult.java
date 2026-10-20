package com.lost.fts.transaction.api.dto;

public record TransactionCreateResult(TransactionResponse transaction,boolean alreadyExisted) {
}
