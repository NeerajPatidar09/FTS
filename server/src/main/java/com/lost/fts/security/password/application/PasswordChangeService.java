package com.lost.fts.security.password.application;

import java.time.LocalDateTime;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lost.fts.security.password.exception.PasswordPolicyValidator;
import com.lost.fts.security.token.application.RefreshTokenService;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordChangeService {
    private final UserRepository userRepository;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final PasswordHistoryService passwordHistoryService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {

        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow();
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid current password");
        }

        passwordPolicyValidator.validate(newPassword);
        passwordHistoryService.validatePasswordNotReused(user, newPassword);
        passwordHistoryService.savePreviousPassword(user);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        refreshTokenService.revokeAllUserTokens(user.getId());

        // Send a password-change notification after commit.
    }
}
