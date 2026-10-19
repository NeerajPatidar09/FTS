package com.lost.fts.transaction.infrastructure.persistence;

public enum TransactionType {
    TRANSFER,
    CASH_DEPOSIT,
    CASH_WITHDRAWAL,
    CARD_PAYMENT,
    PURCHASE,
    REFUND,
    FEE,
    LOAN_REPAYMENT,
    REVERSAL,
    OTHER
}
