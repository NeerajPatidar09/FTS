package com.lost.fts.security.mfa.application;

import com.lost.fts.security.mfa.api.response.MfaSetupResponse;

import java.util.List;

public interface MfaService {

    // MfaSetupResponse setup(String username);

    // List<String> enable(String username, String code);

    // void disable(String username, String code);

    // Long verifyLogin(String challengeToken, String code);

    // List<String> regenerateRecoveryCodes(String username, String code);

    MfaSetupResponse setup(String username, String password);

    List<String> enable(String username, String code);

    void disable(String username, String password, String code);

    List<String> regenerateRecoveryCodes(String username, String password, String code);
}