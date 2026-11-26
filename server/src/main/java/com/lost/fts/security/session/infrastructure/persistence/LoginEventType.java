package com.lost.fts.security.session.infrastructure.persistence;

public enum LoginEventType {

    SUCCESSFUL_LOGIN,
    FAILED_LOGIN,
    NEW_DEVICE_LOGIN,
    UNUSUAL_IP_LOGIN,
    ACCOUNT_LOCKED,
    MFA_FAILURE,
    REFRESH_TOKEN_REUSE,
    PASSWORD_CHANGED,
    PASSWORD_RESET,
    DEVICE_BLOCKED,
    SESSION_REVOKED
}
