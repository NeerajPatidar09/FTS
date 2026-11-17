package com.lost.fts.security.mfa.application;

import com.lost.fts.security.mfa.infrastructure.persistence.*;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import com.lost.fts.security.mfa.api.response.MfaVerificationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.OptionalLong;
import com.lost.fts.security.mfa.infrastructure.persistence.MfaRecoveryCodeRepository;
import com.lost.fts.security.mfa.application.MfaRecoveryCodeGenerator;

@Service
public class MfaLoginServiceImpl implements MfaLoginService {

    private final LoginMfaChallengeRepository challengeRepository;
    private final UserMfaRepository userMfaRepository;
    private final UserRepository userRepository;
    private final TotpService totpService;
    private final MfaEncryptionService encryptionService;
    private final MfaRecoveryCodeRepository recoveryRepository;
    private final MfaRecoveryCodeGenerator recoveryGenerator;

    public MfaLoginServiceImpl(
            LoginMfaChallengeRepository challengeRepository,
            UserMfaRepository userMfaRepository,
            UserRepository userRepository,
            TotpService totpService,
            MfaEncryptionService encryptionService, MfaRecoveryCodeRepository recoveryRepository,
            MfaRecoveryCodeGenerator recoveryGenerator) {
        this.challengeRepository = challengeRepository;
        this.userMfaRepository = userMfaRepository;
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.encryptionService = encryptionService;
        this.recoveryRepository = recoveryRepository;
        this.recoveryGenerator = recoveryGenerator;
    }

    @Override
    @Transactional
    public MfaVerificationResult verify(String rawChallenge, String code) {
        System.out.println("line 43 -- rawChallenge ::::::" + rawChallenge + "  code :::::: " + code);
        if (rawChallenge == null || rawChallenge.isBlank()) {
            return new MfaVerificationResult(false, null);
        }

        String hash = MfaChallengeServiceImpl.hash(rawChallenge);
        System.out.println("line 49 -- hash ::::::" + hash);

        LoginMfaChallenge challenge = challengeRepository.findByHashForUpdate(hash).orElse(null);

        System.out.println("line 53 -- challenge ::::::" + challenge);
        if (challenge == null || challenge.isExpired() || challenge.isConsumed()) {
            System.out.println("line 55 ");
            return new MfaVerificationResult(false, null);
        }

        System.out.println("line 53 -- challenge ::::::" + challenge.getUserId());
        User user = userRepository.findById(challenge.getUserId()).orElse(null);

        if (user == null || !user.isEnabled()) {
            System.out.println("line 62 " + user.isEnabled());
            challenge.consume();
            return new MfaVerificationResult(false, null);
        }

        System.out.println("line 69 " + user.getId());
        UserMfa mfa = userMfaRepository.findByUserIdForUpdate(user.getId()).orElse(null);

        if (mfa == null || !mfa.isEnabled() || !mfa.isVerified()) {
            System.out.println("line 70 ");
            challenge.consume();
            return new MfaVerificationResult(false, null);
        }

        if (mfa.isLocked()) {
            return new MfaVerificationResult(false, null);
        }

        String secret = encryptionService.decrypt(mfa.getSecretEncrypted());

        OptionalLong matchedStep = totpService.findMatchingTimeStep(secret, code, mfa.getLastUsedTimeStep());

        if (matchedStep.isEmpty()) {
            mfa.recordFailure();

            // Return normally so failed-attempt changes commit.
            return new MfaVerificationResult(false, null);
        }

        mfa.recordSuccess(matchedStep.getAsLong());
        challenge.consume();

        return new MfaVerificationResult(true, user.getId());
    }

    @Override
    @Transactional
    public MfaVerificationResult verifyRecoveryCode(
            String rawChallenge,
            String recoveryCode) {

        if (rawChallenge == null || rawChallenge.isBlank()
                || recoveryCode == null || recoveryCode.isBlank()) {
            return new MfaVerificationResult(false, null);
        }

        String challengeHash = MfaChallengeServiceImpl.hash(rawChallenge);

        LoginMfaChallenge challenge = challengeRepository.findByHashForUpdate(challengeHash)
                .orElse(null);

        if (challenge == null
                || challenge.isExpired()
                || challenge.isConsumed()) {
            return new MfaVerificationResult(false, null);
        }

        User user = userRepository.findById(challenge.getUserId())
                .orElse(null);

        if (user == null || !user.isEnabled()) {
            challenge.consume();
            return new MfaVerificationResult(false, null);
        }

        UserMfa mfa = userMfaRepository
                .findByUserIdForUpdate(user.getId())
                .orElse(null);

        if (mfa == null || !mfa.isEnabled() || !mfa.isVerified()) {
            challenge.consume();
            return new MfaVerificationResult(false, null);
        }

        if (mfa.isLocked()) {
            return new MfaVerificationResult(false, null);
        }

        String codeHash = recoveryGenerator.hash(recoveryCode);

        MfaRecoveryCode recoveryCodeEntity = recoveryRepository.findUnusedForUpdate(
                mfa.getId(),
                codeHash).orElse(null);

        if (recoveryCodeEntity == null) {
            mfa.recordFailure();
            return new MfaVerificationResult(false, null);
        }

        recoveryCodeEntity.markUsed();
        challenge.consume();

        return new MfaVerificationResult(true, user.getId());
    }
}