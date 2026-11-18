package com.lost.fts.security.password.application;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lost.fts.security.password.exception.PasswordPolicyException;
import com.lost.fts.security.password.infrastructure.persistence.PasswordHistory;
import com.lost.fts.security.password.infrastructure.persistence.PasswordHistoryRepository;
import com.lost.fts.user.infrastructure.persistence.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordHistoryService {

    private final PasswordHistoryRepository repository;

    private final PasswordEncoder passwordEncoder;

    public void validatePasswordNotReused(User user, String newPassword) {

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {

            throw new PasswordPolicyException("You cannot reuse your current password");
        }

        List<PasswordHistory> history = repository.findTop5ByUserIdOrderByCreatedAtDesc(user.getId());

        for (PasswordHistory item : history) {

            if (passwordEncoder.matches(newPassword, item.getPasswordHash())) {

                throw new PasswordPolicyException("You cannot reuse your previous 5 passwords");
            }
        }
    }

    public void savePreviousPassword(User user) {

        PasswordHistory history = new PasswordHistory();

        history.setUser(user);
        history.setPasswordHash(user.getPasswordHash());
        history.setCreatedAt(LocalDateTime.now());

        repository.save(history);
    }
}