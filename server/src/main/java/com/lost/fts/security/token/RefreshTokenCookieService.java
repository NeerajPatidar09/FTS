package com.lost.fts.security.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieService {

    @Value("${app.jwt.refresh-cookie-name}")
    private String cookieName;

    @Value("${app.jwt.refresh-cookie-secure:true}")
    private boolean secure;

    @Value("${app.jwt.refresh-cookie-same-site:Strict}")
    private String sameSite;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshExpiration;

    public ResponseCookie createCookie(String token) {

        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(secure)
                .path("/api/v1/auth")
                .maxAge(refreshExpiration / 1000)
                .sameSite(sameSite)
                .build();
    }

    public ResponseCookie clearCookie() {

        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(secure)
                .path("/api/v1/auth")
                .maxAge(0)
                .sameSite(sameSite)
                .build();
    }
}