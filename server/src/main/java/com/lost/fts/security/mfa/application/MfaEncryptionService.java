package com.lost.fts.security.mfa.application;

public interface MfaEncryptionService {

        String encrypt(String plainText);

        String decrypt(String encryptedText);
}