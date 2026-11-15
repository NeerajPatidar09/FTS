package com.lost.fts.security.mfa.application;

import com.lost.fts.security.mfa.infrastructure.persistence.*;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class MfaChallengeServiceImpl
        implements MfaChallengeService {

    private final LoginMfaChallengeRepository repository;

    private final SecureRandom secureRandom = new SecureRandom();

    public MfaChallengeServiceImpl(
            LoginMfaChallengeRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public String createChallenge(Long userId) {

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        LoginMfaChallenge challenge = new LoginMfaChallenge();

        challenge.setUserId(userId);
        challenge.setChallengeHash(hash(rawToken));
        challenge.setExpiresAt(Instant.now().plusSeconds(300));

        repository.save(challenge);

        return rawToken;
    }

    public static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] result = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(result);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to hash MFA challenge", ex);
        }
    }
}