package com.lost.fts.security.password.application;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountLockoutService {

    private final UserRepository userRepository;

    private static final int MAX_ATTEMPTS = 5;

    private static final int LOCK_MINUTES = 15;

    @Transactional
    public void recordFailedAttempt(User user) {

        User lockedUser = userRepository
            .findByIdForUpdate(user.getId())
            .orElseThrow();

        int attempts =
            lockedUser.getFailedLoginAttempts() + 1;

        lockedUser.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {

            lockedUser.setLockedUntil(
                LocalDateTime.now()
                    .plusMinutes(LOCK_MINUTES)
            );

            lockedUser.setFailedLoginAttempts(0);
        }
    }

    @Transactional
    public void resetFailedAttempts(User user) {

        User lockedUser = userRepository
            .findByIdForUpdate(user.getId())
            .orElseThrow();

        lockedUser.setFailedLoginAttempts(0);
        lockedUser.setLockedUntil(null);
    }

    public boolean isLocked(User user) {

        return user.getLockedUntil() != null
            && user.getLockedUntil()
                .isAfter(LocalDateTime.now());
    }
}