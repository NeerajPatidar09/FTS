package com.lost.fts.security.mfa.application;

import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class MfaRecoveryCodeGenerator {

    private final SecureRandom random = new SecureRandom();

    public List<String> generate(int count) {

        List<String> codes = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            byte[] bytes = new byte[16];
            random.nextBytes(bytes);

            String code = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(bytes);

            codes.add(code);
        }

        return codes;
    }

    public String hash(String code) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] result = digest.digest(
                    code.getBytes(StandardCharsets.UTF_8));

            return java.util.HexFormat.of().formatHex(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to hash recovery code", ex);
        }
    }
}