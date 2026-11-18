package com.lost.fts.security.encryption;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AesGcmEncryptionService implements FieldEncryptionService {

    private final EncryptionKeyProvider keyProvider;
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int NONCE_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String encrypt(String plainText) {

        if (plainText == null) {
            return null;
        }

        try {

            byte[] nonce = new byte[NONCE_LENGTH];

            secureRandom.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            SecretKey key = keyProvider.currentKey();

            cipher.init(
                Cipher.ENCRYPT_MODE,
                key,
                new GCMParameterSpec(TAG_LENGTH, nonce)
            );

            byte[] encrypted = cipher.doFinal(
                plainText.getBytes(StandardCharsets.UTF_8)
            );

            ByteBuffer buffer = ByteBuffer.allocate(
                nonce.length + encrypted.length
            );

            buffer.put(nonce);
            buffer.put(encrypted);

            return "v1:" + Base64.getEncoder()
                .encodeToString(buffer.array());

        } catch (GeneralSecurityException ex) {

            throw new IllegalStateException(
                "Unable to encrypt sensitive data", ex
            );
        }
    }

    @Override
    public String decrypt(String encryptedText) {

        if (encryptedText == null) {
            return null;
        }

        try {

            if (!encryptedText.startsWith("v1:")) {
                throw new IllegalArgumentException(
                    "Unsupported encrypted data version"
                );
            }

            byte[] combined = Base64.getDecoder().decode(encryptedText.substring(3));
            ByteBuffer buffer = ByteBuffer.wrap(combined);
            byte[] nonce = new byte[NONCE_LENGTH];
            buffer.get(nonce);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keyProvider.currentKey(),new GCMParameterSpec(TAG_LENGTH, nonce));
            byte[] plainText = cipher.doFinal(ciphertext);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException ex) {

            throw new IllegalStateException(
                "Unable to decrypt sensitive data", ex
            );
        }
    }
}