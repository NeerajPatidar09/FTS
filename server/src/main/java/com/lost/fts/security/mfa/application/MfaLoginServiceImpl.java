package com.lost.fts.security.mfa.application;

import com.lost.fts.security.mfa.infrastructure.persistence.*;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import com.lost.fts.security.mfa.api.response.MfaVerificationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.OptionalLong;

@Service
public class MfaLoginServiceImpl implements MfaLoginService {

    private final LoginMfaChallengeRepository challengeRepository;
    private final UserMfaRepository userMfaRepository;
    private final UserRepository userRepository;
    private final TotpService totpService;
    private final MfaEncryptionService encryptionService;

    public MfaLoginServiceImpl(
            LoginMfaChallengeRepository challengeRepository,
            UserMfaRepository userMfaRepository,
            UserRepository userRepository,
            TotpService totpService,
            MfaEncryptionService encryptionService) {
        this.challengeRepository = challengeRepository;
        this.userMfaRepository = userMfaRepository;
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.encryptionService = encryptionService;
    }

    @Override
    @Transactional
    public MfaVerificationResult verify(
            String rawChallenge,
            String code) {

        if (rawChallenge == null || rawChallenge.isBlank()) {
            return new MfaVerificationResult(false, null);
        }

        String hash = MfaChallengeServiceImpl.hash(rawChallenge);

        LoginMfaChallenge challenge = challengeRepository.findByHashForUpdate(hash)
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

        String secret = encryptionService.decrypt(mfa.getSecretEncrypted());

        OptionalLong matchedStep = totpService.findMatchingTimeStep(
                secret,
                code,
                mfa.getLastUsedTimeStep());

        if (matchedStep.isEmpty()) {
            mfa.recordFailure();

            // Return normally so failed-attempt changes commit.
            return new MfaVerificationResult(false, null);
        }

        mfa.recordSuccess(matchedStep.getAsLong());
        challenge.consume();

        return new MfaVerificationResult(true, user.getId());
    }
}