package com.lost.fts.security.mfa.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class MfaEncryptionServiceImpl implements MfaEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int NONCE_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public MfaEncryptionServiceImpl(@Value("${app.security.mfa-encryption-key}") String base64Key) {

        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("MFA encryption key must be 32 bytes");
        }

        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public String encrypt(String plainText) {

        try {
            byte[] nonce = new byte[NONCE_LENGTH];
            secureRandom.nextBytes(nonce);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,
                    new GCMParameterSpec(TAG_LENGTH, nonce));

            byte[] encrypted = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[nonce.length + encrypted.length];

            System.arraycopy(
                    nonce, 0, combined, 0, nonce.length);

            System.arraycopy(
                    encrypted, 0,
                    combined, nonce.length,
                    encrypted.length);

            return "v1:" + Base64.getEncoder()
                    .encodeToString(combined);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to encrypt MFA secret", ex);
        }
    }

    @Override
    public String decrypt(String encryptedText) {

        try {
            if (encryptedText == null ||
                    !encryptedText.startsWith("v1:")) {
                throw new IllegalArgumentException(
                        "Unsupported encrypted secret format");
            }

            byte[] combined = Base64.getDecoder()
                    .decode(encryptedText.substring(3));

            if (combined.length <= NONCE_LENGTH) {
                throw new IllegalArgumentException(
                        "Invalid encrypted secret");
            }

            byte[] nonce = new byte[NONCE_LENGTH];

            System.arraycopy(
                    combined, 0, nonce, 0, NONCE_LENGTH);

            byte[] encrypted = new byte[combined.length - NONCE_LENGTH];

            System.arraycopy(
                    combined,
                    NONCE_LENGTH,
                    encrypted,
                    0,
                    encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(TAG_LENGTH, nonce));

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to decrypt MFA secret", ex);
        }
    }
}