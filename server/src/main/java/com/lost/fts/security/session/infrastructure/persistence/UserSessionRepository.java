package com.lost.fts.security.session.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// @Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findByUserIdAndStatusOrderByLastActivityAtDesc(Long userId, SessionStatus status);

    Optional<UserSession> findBySessionReferenceAndUserId(String sessionReference, Long userId);

    Optional<UserSession> findBySessionReferenceAndStatus(String sessionReference, SessionStatus status);

    Optional<UserSession> findBySessionReference(String sessionReference);

    List<UserSession> findByUserIdAndStatus(Long userId, SessionStatus status);

    Optional<UserSession> findBySessionReferenceAndUser_UsernameAndStatus(String sessionReference, String username,
            SessionStatus status);

    long countByUserIdAndStatus(Long userId, SessionStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE UserSession s SET s.status = :revokedStatus, s.revokedAt = :now, s.revokedReason = :reason WHERE s.user.id = :userId AND s.status = :activeStatus
            """)
    int revokeAllUserSessions(@Param("userId") Long userId, @Param("now") LocalDateTime now,
            @Param("reason") String reason, @Param("activeStatus") SessionStatus activeStatus,
            @Param("revokedStatus") SessionStatus revokedStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE UserSession s
        SET s.lastActivityAt = :now
        WHERE s.sessionReference = :sessionReference
          AND s.status = :status
    """)
    int updateLastActivity(
            @Param("sessionReference") String sessionReference,
            @Param("now") LocalDateTime now,
            @Param("status") SessionStatus status
    );

}