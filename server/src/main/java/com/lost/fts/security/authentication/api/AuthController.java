package com.lost.fts.security.authentication.api;

import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.api.response.LoginResponse;
import com.lost.fts.security.authentication.application.AuthenticationService;
import com.lost.fts.security.authentication.api.response.RefreshResponse;
import com.lost.fts.security.mfa.api.request.MfaLoginRequest;
import jakarta.validation.Valid;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.security.token.RefreshTokenCookieService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.lost.fts.security.authentication.api.response.UserContextResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import com.lost.fts.security.authentication.api.response.LoginResult;
import com.lost.fts.security.token.application.RefreshTokenPair;
import com.lost.fts.security.token.application.RefreshTokenService;
import com.lost.fts.security.mfa.api.response.MfaChallengeResponse;
import com.lost.fts.security.mfa.api.request.MfaRecoveryLoginRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        private final AuthenticationService authenticationService;
        private final RefreshTokenCookieService cookieService;
        private final RefreshTokenService refreshTokenService;
        private final UserRepository userRepository;

        public AuthController(AuthenticationService authenticationService, RefreshTokenCookieService cookieService,
                        RefreshTokenService refreshTokenService, UserRepository userRepository) {
                this.authenticationService = authenticationService;
                this.cookieService = cookieService;
                this.refreshTokenService = refreshTokenService;
                this.userRepository = userRepository;
        }

        // @PostMapping("/login")
        // @ResponseStatus(HttpStatus.OK)
        // public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        // return authenticationService.login(request);
        // }

        @PostMapping("/login")
        public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

                LoginResult result = authenticationService.login(request);

                if (result.mfaRequired()) {
                        return ResponseEntity.ok(new MfaChallengeResponse(true, result.challengeToken(), 300,
                                        "MFA verification required"));
                }

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE,
                                                cookieService.createCookie(result.refreshToken()).toString())
                                .body(result.loginResponse());
        }

        @PostMapping("/verify-mfa")
        public ResponseEntity<LoginResponse> verifyMfa(@Valid @RequestBody MfaLoginRequest request) {
                LoginResult result = authenticationService.completeMfaLogin(request.challengeToken(), request.code());
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE,
                                                cookieService.createCookie(result.refreshToken()).toString())
                                .body(result.loginResponse());
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
                RefreshTokenPair pair = refreshTokenService.rotateRefreshToken(refreshToken);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE,
                                                cookieService.createCookie(pair.refreshToken()).toString())
                                .body(new RefreshResponse(pair.accessToken(), "Bearer", pair.expiresIn()));
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout(
                        @CookieValue(name = "refreshToken", required = false) String refreshToken) {

                refreshTokenService.revokeRefreshToken(refreshToken);

                return ResponseEntity.noContent()
                                .header(
                                                HttpHeaders.SET_COOKIE,
                                                cookieService.clearCookie().toString())
                                .build();
        }

        @PostMapping("/logout-all")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<Void> logoutAll(Authentication authentication) {
                // System.out.println(authentication.getName());
                User user = userRepository.findByUsernameIgnoreCase(authentication.getName()).orElseThrow();
                // System.out.println("user id :::::::: " + user.getId());
                refreshTokenService.revokeAllUserTokens(user.getId());

                return ResponseEntity.noContent()
                                .header(
                                                HttpHeaders.SET_COOKIE,
                                                cookieService.clearCookie().toString())
                                .build();
        }

        @PostMapping("/verify-mfa-recovery")
        public ResponseEntity<LoginResponse> verifyMfaRecovery(@Valid @RequestBody MfaRecoveryLoginRequest request) {

                LoginResult result = authenticationService.completeRecoveryLogin(request.challengeToken(),
                                request.recoveryCode());
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE,
                                                cookieService.createCookie(result.refreshToken()).toString())
                                .body(result.loginResponse());
        }

        // @GetMapping("/me")
        // public UserContextResponse getCurrentUser(Authentication authentication) {
        // return authenticationService.getCurrentUser(authentication.getName());
        // }
}