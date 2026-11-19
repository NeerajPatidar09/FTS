package com.lost.fts.security.mfa.application;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.OptionalLong;

@Service
public class TotpServiceImpl implements TotpService {

        // private static final int PERIOD_SECONDS = 30;
        // private static final int CODE_LENGTH = 6;

        private final SecureRandom random = new SecureRandom();

        @Override
        public String generateSecret() {

                byte[] bytes = new byte[20];

                random.nextBytes(bytes);

                return new Base32()
                                .encodeToString(bytes)
                                .replace("=", "");
        }

        @Override
        public OptionalLong findMatchingTimeStep(String secret, String code, Long lastUsedTimeStep) {
                if (code == null || !code.matches("\\d{6}")) {
                        return OptionalLong.empty();
                }

                long currentStep = Instant.now().getEpochSecond() / 30;
                long lastUsed = lastUsedTimeStep == null ? -1 : lastUsedTimeStep;
                // System.out.println("lastUsed ::::: " + lastUsed + " currentStep :::::" + currentStep + "  code: : : "
                                // + code + "  lastUsedTimeStep::: " + lastUsedTimeStep);
                for (long step = currentStep - 1; step <= currentStep + 1; step++) {

                        if (step <= lastUsed) {
                                continue;
                        }
                        String expected = generateCode(secret, step);
                        // System.out.println("expected ::::: " + expected);
                        if (java.security.MessageDigest.isEqual(
                                        expected.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
                                        code.getBytes(java.nio.charset.StandardCharsets.US_ASCII))) {
                                return OptionalLong.of(step);
                        }
                }

                return OptionalLong.empty();
        }

        @Override
        public boolean verifyCode(String secret, String code, long lastUsedTimeStep) {
                return findMatchingTimeStep(secret, code, lastUsedTimeStep).isPresent();
        }

        private String generateCode(String secret, long step) {

                try {
                        byte[] key = new Base32().decode(secret);
                        byte[] counter = ByteBuffer.allocate(8).putLong(step).array();
                        Mac mac = Mac.getInstance("HmacSHA1");
                        mac.init(new SecretKeySpec(key, "HmacSHA1"));
                        byte[] hash = mac.doFinal(counter);
                        int offset = hash[hash.length - 1] & 0x0F;
                        int binary = ((hash[offset] & 0x7F) << 24)
                                        | ((hash[offset + 1] & 0xFF) << 16)
                                        | ((hash[offset + 2] & 0xFF) << 8)
                                        | (hash[offset + 3] & 0xFF);

                        int otp = binary % 1_000_000;
                        return String.format("%06d", otp);

                } catch (Exception ex) {
                        throw new IllegalStateException("Unable to generate TOTP", ex);
                }
        }

        @Override
        public String createProvisioningUri(
                        String username,
                        String secret) {

                String issuer = "Stranger";

                return "otpauth://totp/"
                                + java.net.URLEncoder.encode(
                                                issuer + ":" + username,
                                                java.nio.charset.StandardCharsets.UTF_8)
                                + "?secret=" + secret
                                + "&issuer=" + java.net.URLEncoder.encode(
                                                issuer,
                                                java.nio.charset.StandardCharsets.UTF_8)
                                + "&algorithm=SHA1&digits=6&period=30";
        }
}