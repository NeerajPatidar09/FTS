package com.lost.fts.security.password.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    List<PasswordHistory> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByUserId(Long userId);
}