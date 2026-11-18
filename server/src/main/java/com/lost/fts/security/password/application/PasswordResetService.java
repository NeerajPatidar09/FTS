package com.lost.fts.security.password.application;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lost.fts.security.password.exception.PasswordPolicyValidator;
import com.lost.fts.security.password.infrastructure.persistence.PasswordResetToken;
import com.lost.fts.security.password.infrastructure.persistence.PasswordResetTokenRepository;
import com.lost.fts.security.token.application.RefreshTokenService;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

        private final UserRepository userRepository;
        private final PasswordResetTokenRepository tokenRepository;
        private final PasswordResetTokenService tokenService;
        private final JavaMailSender mailSender;
        private final PasswordPolicyValidator passwordPolicyValidator;
        private final PasswordHistoryService passwordHistoryService;
        private final RefreshTokenService refreshTokenService;
        private final PasswordEncoder passwordEncoder;

        @Value("${app.frontend-url}")
        private String frontendUrl;

        @Transactional
        public void forgotPassword(String email) {

                userRepository.findByEmail(email).filter(User::isEnabled).ifPresent(user -> {

                        // Invalidate previous reset tokens.
                        tokenRepository.deleteByUserId(user.getId());
                        String rawToken = tokenService.generateToken();
                        PasswordResetToken resetToken = new PasswordResetToken();
                        resetToken.setUser(user);
                        resetToken.setTokenHash(tokenService.hashToken(rawToken));
                        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
                        resetToken.setCreatedAt(LocalDateTime.now());
                        tokenRepository.save(resetToken);
                        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
                        sendResetEmail(user.getEmail(), resetLink);
                });
        }

        private void sendResetEmail(String email, String resetLink) {

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Stranger Password Reset");
                message.setText(
                                "Use the following link to reset your password:" + resetLink
                                                + "\nThis link expires in 15 minutes.");

                mailSender.send(message);
        }

        @Transactional
        public void resetPassword(String rawToken, String newPassword) {

                passwordPolicyValidator.validate(newPassword);
                String tokenHash = tokenService.hashToken(rawToken);

                PasswordResetToken resetToken = tokenRepository.findByTokenHashForUpdate(tokenHash)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Invalid or expired reset token"));

                if (resetToken.getUsedAt() != null ||
                                resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {

                        throw new IllegalArgumentException(
                                        "Invalid or expired reset token");
                }

                User user = resetToken.getUser();

                passwordHistoryService.validatePasswordNotReused(user, newPassword);
                passwordHistoryService.savePreviousPassword(user);
                user.setPasswordHash(passwordEncoder.encode(newPassword));
                user.setPasswordChangedAt(LocalDateTime.now());
                resetToken.setUsedAt(LocalDateTime.now());
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                // Invalidate every outstanding reset token.
                tokenRepository.deleteByUserId(user.getId());
                // Revoke all existing refresh tokens.
                refreshTokenService.revokeAllUserTokens(user.getId());
        }

}