// package com.lost.fts.security.mfa.api;

// import com.lost.fts.security.mfa.application.*;
// import com.lost.fts.security.mfa.api.response.*;
// import com.lost.fts.security.mfa.api.request.*;
// import jakarta.validation.Valid;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.security.core.Authentication;

// @RestController
// @RequestMapping("/api/v1/auth/mfa")
// public class MfaController {

//     private final MfaService mfaService;

//     public MfaController(MfaService mfaService) {
//         this.mfaService = mfaService;
//     }

//     @PostMapping("/setup")
//     public MfaSetupResponse setup(Authentication authentication) {
//         return mfaService.setup(authentication.getName());
//     }

//     @PostMapping("/enable")
//     public void enable(@Valid @RequestBody MfaVerifyRequest request, Authentication authentication) {
//         mfaService.enable(authentication.getName(), request.code());
//     }

//     @PostMapping("/disable")
//     public void disable(@Valid @RequestBody MfaVerifyRequest request, Authentication authentication) {
//         mfaService.disable(authentication.getName(), request.code());
//     }
// }

package com.lost.fts.security.mfa.api;

import com.lost.fts.security.mfa.api.request.*;
import com.lost.fts.security.mfa.api.response.MfaSetupResponse;
import com.lost.fts.security.mfa.application.MfaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.lost.fts.security.authentication.api.response.LoginResponse;
import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.application.AuthenticationService;
import org.springframework.http.HttpHeaders;
import com.lost.fts.security.mfa.api.request.*;
import com.lost.fts.security.mfa.api.response.MfaSetupResponse;
import com.lost.fts.security.mfa.application.MfaService;
import jakarta.validation.Valid;
import com.lost.fts.security.token.RefreshTokenCookieService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.lost.fts.security.authentication.api.response.LoginResult;
import java.security.SecureRandom;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/auth/mfa")
public class MfaController {

        private final MfaService mfaService;
        private final AuthenticationService authenticationService;
        private final RefreshTokenCookieService cookieService;

        private final SecureRandom secureRandom = new SecureRandom();

        public MfaController(MfaService mfaService, AuthenticationService authenticationService,
                        RefreshTokenCookieService cookieService) {
                this.mfaService = mfaService;
                this.authenticationService = authenticationService;
                this.cookieService = cookieService;
        }

        @PostMapping("/setup")
        public MfaSetupResponse setup(@Valid @RequestBody MfaSetupRequest request, Authentication authentication) {
                return mfaService.setup(authentication.getName(), request.password());
        }

        // private String generateChallengeToken() {
        // byte[] bytes = new byte[32];
        // secureRandom.nextBytes(bytes);

        // String rawToken = Base64.getUrlEncoder()
        // .withoutPadding()
        // .encodeToString(bytes);
        // return rawToken;
        // }

        @PostMapping("/enable")
        public ResponseEntity<List<String>> enable(@Valid @RequestBody MfaVerifyRequest request,
                        Authentication authentication) {
                List<String> recoveryCodes = mfaService.enable(authentication.getName(), request.code());
                // // System.out.println(generateChallengeToken());
                return ResponseEntity.ok(recoveryCodes);
        }

        @PostMapping("/disable")
        public ResponseEntity<Void> disable(
                        @Valid @RequestBody MfaSecurityRequest request,
                        Authentication authentication) {
                mfaService.disable(
                                authentication.getName(),
                                request.password(),
                                request.code());

                return ResponseEntity.noContent().build();
        }

        @PostMapping("/recovery-codes/regenerate")
        public ResponseEntity<List<String>> regenerateRecoveryCodes(@Valid @RequestBody MfaSecurityRequest request,
                        Authentication authentication) {
                return ResponseEntity.ok(mfaService.regenerateRecoveryCodes(authentication.getName(),
                                request.password(), request.code()));
        }

        @PostMapping("/verify-mfa-recovery")
        public ResponseEntity<LoginResponse> verifyMfaRecovery(
                        @Valid @RequestBody MfaRecoveryLoginRequest request) {

                LoginResult result = authenticationService.completeRecoveryLogin(
                                request.challengeToken(),
                                request.recoveryCode());

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.SET_COOKIE,
                                                cookieService.createCookie(
                                                                result.refreshToken()).toString())
                                .body(result.loginResponse());
        }
}