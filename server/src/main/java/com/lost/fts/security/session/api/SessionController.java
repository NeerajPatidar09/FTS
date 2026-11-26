package com.lost.fts.security.session.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.lost.fts.security.session.api.response.SessionResponse;
import com.lost.fts.security.session.application.SessionService;
import com.lost.fts.security.userdetails.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/sessions")
@RequiredArgsConstructor
public class SessionController {

        private final SessionService sessionService;

        @GetMapping
        public ResponseEntity<List<SessionResponse>> getSessions(
                        @AuthenticationPrincipal CustomUserDetails principal) {

                return ResponseEntity.ok(
                                sessionService.getActiveSessionResponses(
                                                principal.getUser().getId(),
                                                principal.getSessionReference()));
        }

        // @GetMapping("/current")
        // public ResponseEntity<?> getCurrentSession(
        //                 @AuthenticationPrincipal CustomUserDetails principal) {
        //         String sessionReference = principal.getSessionReference();

        //         return ResponseEntity.ok(
        //                         sessionService.getSessionByReference(sessionReference));
        // }

        @DeleteMapping("/{reference}")
        public ResponseEntity<?> revokeSession(
                        @AuthenticationPrincipal CustomUserDetails principal,
                        @PathVariable String reference) {

                sessionService.revokeSession(
                                principal.getUser().getId(),
                                reference);

                return ResponseEntity.ok(
                                Map.of("message", "Session revoked successfully"));
        }

        @DeleteMapping
        public ResponseEntity<?> revokeAllSessions(
                        @AuthenticationPrincipal CustomUserDetails principal) {

                sessionService.revokeAllSessions(
                                principal.getUser().getId(),
                                "USER_LOGOUT_ALL");

                return ResponseEntity.ok(
                                Map.of("message", "All sessions revoked successfully"));
        }
}