package com.lost.fts.transaction.infrastructure.persistence;

public enum TransactionType {
    TRANSFER,
    CASHDEPOSIT,
    CASHWITHDRAWAL,
    CARDPAYMENT,
    PURCHASE,
    REFUND,
    FEE,
    LOANREPAYMENT,
    REVERSAL,
    OTHER
}
