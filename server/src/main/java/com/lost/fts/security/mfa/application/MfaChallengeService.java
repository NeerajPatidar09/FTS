package com.lost.fts.security.mfa.application;

public interface MfaChallengeService {

    String createChallenge(Long userId);
}