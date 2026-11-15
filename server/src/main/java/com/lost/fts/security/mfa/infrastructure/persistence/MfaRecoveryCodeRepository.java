package com.lost.fts.security.mfa.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MfaRecoveryCodeRepository extends JpaRepository<MfaRecoveryCode, Long> {

    List<MfaRecoveryCode> findByUserMfaIdAndUsedFalse(Long userMfaId);

    void deleteAllByUserMfaId(Long userMfaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT r
                FROM MfaRecoveryCode r
                WHERE r.userMfa.id = :mfaId
                  AND r.used = false
            """)
    List<MfaRecoveryCode> findUnusedCodesForUpdate(
            @Param("mfaId") Long mfaId);
}