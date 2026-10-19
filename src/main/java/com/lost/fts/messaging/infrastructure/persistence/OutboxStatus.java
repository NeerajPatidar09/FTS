package com.lost.fts.messaging.infrastructure.persistence;

public enum OutboxStatus {
    PENDING,
    INPROGRESS,
    PUBLISHED,
    FAILED
}
