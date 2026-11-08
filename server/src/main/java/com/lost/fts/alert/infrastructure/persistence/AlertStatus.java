package com.lost.fts.alert.infrastructure.persistence;

public enum AlertStatus {
    NEW,
    ACKNOWLEDGED,
    UNDERINVESTIGATION,
    ESCALATED,
    RESOLVED,
    FALSEPOSITIVE,
    CLOSED
}
