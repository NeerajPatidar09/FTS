package com.lost.fts.security.password.api;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lost.fts.security.password.api.request.ChangePasswordRequest;
import com.lost.fts.security.password.api.request.ForgotPasswordRequest;
import com.lost.fts.security.password.api.request.ResetPasswordRequest;
import com.lost.fts.security.password.application.PasswordChangeService;
import com.lost.fts.security.password.application.PasswordResetService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
// @RequiredArgsConstructor
public class PasswordController {

    private final PasswordResetService passwordResetService;
    private final PasswordChangeService passwordChangeService;

    public PasswordController(PasswordResetService passwordResetService, PasswordChangeService passwordChangeService){
        this.passwordResetService = passwordResetService;
        this.passwordChangeService = passwordChangeService;
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.forgotPassword(request.email());
        return ResponseEntity.ok(
                Map.of("message", "If an account exists with this email, password reset instructions will be sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        passwordChangeService.changePassword(principal.getUsername(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}