package com.lost.fts.security.mfa.application;

import com.lost.fts.security.mfa.api.response.MfaVerificationResult;

public interface MfaLoginService {

    MfaVerificationResult verify(
            String challengeToken,
            String code);
}