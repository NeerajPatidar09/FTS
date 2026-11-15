package com.lost.fts.security.mfa.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.lost.fts.security.mfa.infrastructure.persistence.UserMfa;

import java.util.Optional;

public interface UserMfaRepository extends JpaRepository<UserMfa, Long> {

    Optional<UserMfa> findByUserId(Long userId);

    // Optional<UserMfa> findByUserUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT m
                FROM UserMfa m
                WHERE m.userId = :userId
            """)
    Optional<UserMfa> findByUserIdForUpdate(
            @Param("userId") Long userId);
}