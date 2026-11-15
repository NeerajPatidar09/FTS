package com.lost.fts.security.mfa.application;

import java.util.OptionalLong;

public interface TotpService {

        String generateSecret();

        OptionalLong findMatchingTimeStep(String secret, String code, Long lastUsedTimeStep);

        String createProvisioningUri(String username, String secret);

        public boolean verifyCode(String secret, String code, long lastUsedTimeStep);
}