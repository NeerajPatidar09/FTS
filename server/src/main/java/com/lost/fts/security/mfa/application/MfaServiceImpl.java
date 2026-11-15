package com.lost.fts.security.mfa.application;

import com.lost.fts.security.mfa.api.response.MfaSetupResponse;
import com.lost.fts.security.mfa.application.MfaEncryptionService;
import com.lost.fts.security.mfa.infrastructure.persistence.UserMfa;
import com.lost.fts.security.mfa.infrastructure.persistence.MfaRecoveryCode;
import com.lost.fts.security.mfa.infrastructure.persistence.LoginMfaChallenge;
import com.lost.fts.security.mfa.infrastructure.persistence.UserMfaRepository;
import com.lost.fts.security.mfa.infrastructure.persistence.MfaRecoveryCodeRepository;
import com.lost.fts.security.mfa.infrastructure.persistence.LoginMfaChallengeRepository;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
public class MfaServiceImpl implements MfaService {

        private static final int RECOVERY_CODE_COUNT = 10;

        private final UserRepository userRepository;

        private final UserMfaRepository userMfaRepository;

        private final MfaRecoveryCodeRepository recoveryCodeRepository;

        private final LoginMfaChallengeRepository challengeRepository;

        private final TotpService totpService;

        private final MfaEncryptionService encryptionService;

        private final SecureRandom secureRandom = new SecureRandom();

        public MfaServiceImpl(
                        UserRepository userRepository,
                        UserMfaRepository userMfaRepository,
                        MfaRecoveryCodeRepository recoveryCodeRepository,
                        LoginMfaChallengeRepository challengeRepository,
                        TotpService totpService,
                        MfaEncryptionService encryptionService) {
                this.userRepository = userRepository;
                this.userMfaRepository = userMfaRepository;
                this.recoveryCodeRepository = recoveryCodeRepository;
                this.challengeRepository = challengeRepository;
                this.totpService = totpService;
                this.encryptionService = encryptionService;
        }

        // ------------------------------------------------
        // 1. MFA SETUP
        // ------------------------------------------------

        @Override
        @Transactional
        public MfaSetupResponse setup(String username) {

                User user = findUser(username);
                UserMfa userMfa = userMfaRepository.findByUserIdForUpdate(user.getId()).orElseGet(UserMfa::new);

                if (userMfa.isEnabled()) {
                        throw new IllegalStateException("MFA is already enabled");
                }

                String secret = totpService.generateSecret();
                userMfa.setUserId(user.getId());
                // userMfa.setUser(user);

                userMfa.setSecretEncrypted(encryptionService.encrypt(secret));
                userMfa.setEnabled(false);
                userMfa.setVerified(false);
                userMfa.setLastUsedTimeStep(Instant.now().getEpochSecond() / 30);
                userMfa.setFailedAttempts(0);
                userMfa.setLockedUntil(null);
                userMfaRepository.save(userMfa);

                String provisioningUri = totpService.createProvisioningUri(
                                user.getUsername(),
                                secret);

                return new MfaSetupResponse(
                                secret,
                                provisioningUri,
                                "Add this account to your authenticator application");
        }

        // ------------------------------------------------
        // 2. ENABLE MFA
        // ------------------------------------------------

        @Override
        @Transactional
        public List<String> enable(String username, String code) {

                User user = findUser(username);
                UserMfa userMfa = userMfaRepository
                                .findByUserIdForUpdate(user.getId())
                                .orElseThrow(() -> new IllegalStateException(
                                                "MFA setup not found. Please start setup first."));

                if (userMfa.isEnabled()) {
                        throw new IllegalStateException(
                                        "MFA is already enabled");
                }

                if (userMfa.getSecretEncrypted() == null) {
                        throw new IllegalStateException(
                                        "MFA secret is not configured");
                }

                String secret = encryptionService.decrypt(userMfa.getSecretEncrypted());

                long matchedStep = verifyTotp(secret, code, userMfa.getLastUsedTimeStep());

                userMfa.setLastUsedTimeStep(matchedStep);
                userMfa.setEnabled(true);
                userMfa.setVerified(true);
                userMfa.setFailedAttempts(0);
                userMfa.setLockedUntil(null);

                userMfaRepository.save(userMfa);
                return createRecoveryCodes(userMfa);
        }

        // ------------------------------------------------
        // 3. DISABLE MFA
        // ------------------------------------------------

        @Override
        @Transactional
        public void disable(
                        String username,
                        String code) {

                User user = findUser(username);

                UserMfa userMfa = userMfaRepository
                                .findByUserIdForUpdate(user.getId())
                                .orElseThrow(() -> new IllegalStateException(
                                                "MFA configuration not found"));

                if (!userMfa.isEnabled()) {
                        throw new IllegalStateException(
                                        "MFA is not enabled");
                }

                String secret = encryptionService.decrypt(
                                userMfa.getSecretEncrypted());

                long matchedStep = verifyTotp(
                                secret,
                                code,
                                userMfa.getLastUsedTimeStep());

                userMfa.setLastUsedTimeStep(matchedStep);
                userMfa.setEnabled(false);
                userMfa.setVerified(false);
                userMfa.setSecretEncrypted(null);
                userMfa.setLastUsedTimeStep(null);

                recoveryCodeRepository.deleteAllByUserMfaId(
                                userMfa.getId());

                userMfaRepository.save(userMfa);
        }

        // ------------------------------------------------
        // 4. VERIFY MFA LOGIN
        // ------------------------------------------------

        @Override
        @Transactional
        public Long verifyLogin(String challengeToken, String code) {

                if (challengeToken == null || challengeToken.isBlank()) {
                        throw new IllegalArgumentException(
                                        "MFA challenge token is required");
                }

                if (code == null || code.isBlank()) {
                        throw new IllegalArgumentException(
                                        "MFA verification code is required");
                }

                String challengeHash = hashToken(challengeToken);

                LoginMfaChallenge challenge = challengeRepository
                                .findByHashForUpdate(challengeHash)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Invalid MFA challenge"));

                if (challenge.getConsumedAt() != null) {
                        throw new IllegalArgumentException(
                                        "MFA challenge has already been used");
                }

                if (!challenge.getExpiresAt().isAfter(
                                java.time.Instant.now())) {
                        throw new IllegalArgumentException(
                                        "MFA challenge has expired");
                }

                User user = userRepository.findById(
                                challenge.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                if (!user.isEnabled()) {
                        throw new IllegalStateException(
                                        "User account is disabled");
                }

                UserMfa userMfa = userMfaRepository
                                .findByUserIdForUpdate(user.getId())
                                .orElseThrow(() -> new IllegalStateException(
                                                "MFA configuration not found"));

                if (!userMfa.isEnabled() || !userMfa.isVerified()) {
                        throw new IllegalStateException(
                                        "MFA is not enabled for this account");
                }

                boolean verified = false;

                if (code.matches("\\d{6}")) {

                        String secret = encryptionService.decrypt(
                                        userMfa.getSecretEncrypted());

                        long matchedStep = findMatchingStep(
                                        secret,
                                        code,
                                        userMfa.getLastUsedTimeStep());

                        if (matchedStep >= 0) {
                                userMfa.setLastUsedTimeStep(matchedStep);
                                verified = true;
                        }
                }

                if (!verified) {
                        verified = verifyRecoveryCode(userMfa, code);
                }

                if (!verified) {
                        throw new IllegalArgumentException(
                                        "Invalid MFA verification code");
                }

                // Mark challenge as consumed only after successful verification.
                challenge.setConsumedAt(java.time.Instant.now());

                userMfa.setFailedAttempts(0);

                challengeRepository.save(challenge);
                userMfaRepository.save(userMfa);

                return user.getId();
        }

        // ------------------------------------------------
        // 5. REGENERATE RECOVERY CODES
        // ------------------------------------------------

        @Override
        @Transactional
        public List<String> regenerateRecoveryCodes(String username, String code) {

                User user = findUser(username);

                UserMfa userMfa = userMfaRepository
                                .findByUserIdForUpdate(user.getId())
                                .orElseThrow(() -> new IllegalStateException(
                                                "MFA configuration not found"));

                if (!userMfa.isEnabled()) {
                        throw new IllegalStateException(
                                        "MFA is not enabled");
                }

                String secret = encryptionService.decrypt(
                                userMfa.getSecretEncrypted());

                long matchedStep = verifyTotp(
                                secret,
                                code,
                                userMfa.getLastUsedTimeStep());

                userMfa.setLastUsedTimeStep(matchedStep);

                recoveryCodeRepository.deleteAllByUserMfaId(
                                userMfa.getId());

                return createRecoveryCodes(userMfa);
        }

        // ------------------------------------------------
        // PRIVATE HELPER METHODS
        // ------------------------------------------------

        private User findUser(String username) {

                return userRepository
                                .findByUsernameIgnoreCase(username)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "User not found"));
        }

        /**
         * Verifies a TOTP and returns the matching time step.
         * A previously used time step cannot be reused.
         */
        private long verifyTotp(String secret, String code, Long lastUsedTimeStep) {

                long matchedStep = findMatchingStep(secret, code, lastUsedTimeStep);

                if (matchedStep < 0) {
                        throw new IllegalArgumentException("Invalid or already used OTP");
                }

                return matchedStep;
        }

        private long findMatchingStep(String secret, String code, Long lastUsedTimeStep) {

                long lastStep = lastUsedTimeStep == null ? -1 : lastUsedTimeStep;
                return totpService.findMatchingTimeStep(secret, code, lastStep).orElse(-1L);
        }

        /**
         * Creates ten single-use recovery codes.
         * Only their SHA-256 hashes are saved.
         */
        private List<String> createRecoveryCodes(UserMfa userMfa) {
                recoveryCodeRepository.deleteAllByUserMfaId(userMfa.getId());
                List<String> rawCodes = new ArrayList<>();
                List<MfaRecoveryCode> recoveryCodes = new ArrayList<>();
                for (int i = 0; i < RECOVERY_CODE_COUNT; i++) {
                        String rawCode = generateRecoveryCode();
                        MfaRecoveryCode entity = new MfaRecoveryCode();
                        entity.setUserMfa(userMfa);
                        entity.setCodeHash(hashToken(rawCode));
                        entity.setUsed(false);
                        rawCodes.add(rawCode);
                        recoveryCodes.add(entity);
                }

                recoveryCodeRepository.saveAll(recoveryCodes);

                // These raw codes must be displayed only once.
                return rawCodes;
        }

        /**
         * Checks a recovery code and consumes it if valid.
         */
        private boolean verifyRecoveryCode(
                        UserMfa userMfa,
                        String rawCode) {

                if (rawCode == null || rawCode.isBlank()) {
                        return false;
                }

                String submittedHash = hashToken(rawCode);

                List<MfaRecoveryCode> availableCodes = recoveryCodeRepository
                                .findByUserMfaIdAndUsedFalse(
                                                userMfa.getId());

                for (MfaRecoveryCode recoveryCode : availableCodes) {

                        boolean matches = MessageDigest.isEqual(
                                        submittedHash.getBytes(StandardCharsets.UTF_8),
                                        recoveryCode.getCodeHash()
                                                        .getBytes(StandardCharsets.UTF_8));

                        if (matches) {
                                recoveryCode.markUsed();

                                recoveryCodeRepository.save(recoveryCode);

                                return true;
                        }
                }

                return false;
        }

        private String generateRecoveryCode() {

                byte[] bytes = new byte[16];

                secureRandom.nextBytes(bytes);

                String value = Base64.getUrlEncoder()
                                .withoutPadding()
                                .encodeToString(bytes);

                return value.substring(0, 8).toUpperCase()
                                + "-"
                                + value.substring(8, 16).toUpperCase();
        }

        private String hashToken(String token) {

                try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");

                        byte[] hash = digest.digest(
                                        token.getBytes(StandardCharsets.UTF_8));

                        return HexFormat.of().formatHex(hash);

                } catch (Exception ex) {
                        throw new IllegalStateException(
                                        "Unable to hash token", ex);
                }
        }
}