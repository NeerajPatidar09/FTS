package com.lost.fts.security.encryption;

public interface FieldEncryptionService {

    String encrypt(String plainText);
    String decrypt(String encryptedText);
}