package com.lost.fts.security.authentication.application;

import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.api.response.LoginResult;

public interface AuthenticationService {
    LoginResult login(LoginRequest request);

    LoginResult completeMfaLogin(String challengeToken, String code);
}
