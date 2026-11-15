package com.lost.fts.security.mfa.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginMfaChallengeRepository
        extends JpaRepository<LoginMfaChallenge, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT c
                FROM LoginMfaChallenge c
                WHERE c.challengeHash = :hash
            """)
    Optional<LoginMfaChallenge> findByHashForUpdate(
            @Param("hash") String hash);

}