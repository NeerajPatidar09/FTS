package com.lost.fts.security.mfa.application;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class MfaEncryptionService {

    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION =
            "AES/GCM/NoPadding";

    private static final int NONCE_LENGTH = 12;

    private static final int TAG_LENGTH = 128;

    private static final byte[] AAD =
            "ReguShield:MFA:v1".getBytes(StandardCharsets.UTF_8);

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.security.mfa-encryption-key}")
    private String encryptionKey;

    private SecretKeySpec secretKey;

    @PostConstruct
    public void initialize() {

        try {
            byte[] decodedKey =
                    Base64.getDecoder().decode(encryptionKey);

            if (decodedKey.length != 32) {
                throw new IllegalStateException(
                        "MFA encryption key must be exactly 32 bytes"
                );
            }

            this.secretKey = new SecretKeySpec(
                    decodedKey,
                    ALGORITHM
            );

            Arrays.fill(decodedKey, (byte) 0);

        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Invalid Base64 MFA encryption key", ex
            );
        }
    }

    /**
     * Encrypts the MFA secret using AES-256-GCM.
     */
    public String encrypt(String plainText) {

        if (plainText == null || plainText.isBlank()) {
            throw new IllegalArgumentException(
                    "MFA secret cannot be empty"
            );
        }

        try {
            byte[] nonce = new byte[NONCE_LENGTH];

            secureRandom.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(TAG_LENGTH, nonce);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    parameterSpec
            );

            cipher.updateAAD(AAD);

            byte[] encryptedData = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8)
            );

            ByteBuffer buffer = ByteBuffer.allocate(
                    nonce.length + encryptedData.length
            );

            buffer.put(nonce);
            buffer.put(encryptedData);

            return Base64.getEncoder()
                    .encodeToString(buffer.array());

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to encrypt MFA secret", ex
            );
        }
    }

    /**
     * Decrypts the stored MFA secret.
     */
    public String decrypt(String encryptedText) {

        if (encryptedText == null || encryptedText.isBlank()) {
            throw new IllegalArgumentException(
                    "Encrypted MFA secret cannot be empty"
            );
        }

        try {
            byte[] encryptedBytes =
                    Base64.getDecoder().decode(encryptedText);

            if (encryptedBytes.length <= NONCE_LENGTH) {
                throw new IllegalArgumentException(
                        "Invalid encrypted MFA secret"
                );
            }

            ByteBuffer buffer =
                    ByteBuffer.wrap(encryptedBytes);

            byte[] nonce = new byte[NONCE_LENGTH];

            buffer.get(nonce);

            byte[] encryptedData =
                    new byte[buffer.remaining()];

            buffer.get(encryptedData);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(TAG_LENGTH, nonce);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    parameterSpec
            );

            cipher.updateAAD(AAD);

            byte[] decryptedData =
                    cipher.doFinal(encryptedData);

            return new String(
                    decryptedData,
                    StandardCharsets.UTF_8
            );

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to decrypt MFA secret", ex
            );
        }
    }
}