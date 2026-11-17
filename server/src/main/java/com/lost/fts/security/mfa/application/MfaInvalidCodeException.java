package com.lost.fts.security.mfa.application;

public class MfaInvalidCodeException extends RuntimeException {

    public MfaInvalidCodeException() {
        super("Invalid or expired MFA code");
    }
}