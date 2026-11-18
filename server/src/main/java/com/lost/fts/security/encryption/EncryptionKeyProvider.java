package com.lost.fts.security.encryption;

import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class EncryptionKeyProvider {
     private final Map<String, SecretKey> keys;

    public EncryptionKeyProvider(
            @Value("${app.encryption.key-v1}") String keyV1) {

        this.keys = Map.of(
            "v1", createKey(keyV1)
        );
    }


    public SecretKey currentKey() {
        return keyForVersion("v1");
    }

    public SecretKey keyForVersion(String version) {

        SecretKey key = keys.get(version);

        if (key == null) {
            throw new IllegalStateException(
                "Encryption key version not configured: " + version
            );
        }

        return key;
    }

    private SecretKey createKey(String encodedKey) {

        try {
            byte[] decoded = Base64.getDecoder()
                .decode(encodedKey);

            if (decoded.length != 32) {
                throw new IllegalArgumentException(
                    "AES-256 key must contain exactly 32 bytes"
                );
            }

            return new SecretKeySpec(decoded, "AES");

        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                "Invalid AES encryption key configuration", ex
            );
        }
    }
}