package com.lost.fts.security.authentication.application;

import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.api.response.LoginResult;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    LoginResult login(LoginRequest request, HttpServletRequest httpRequest);

    LoginResult completeMfaLogin(String challengeToken, String code, HttpServletRequest request);

    LoginResult completeRecoveryLogin(String challengeToken, String recoveryCode, HttpServletRequest request);
}
