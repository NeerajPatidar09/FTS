package com.lost.fts.security.mfa.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MfaRecoveryCodeRepository extends JpaRepository<MfaRecoveryCode, Long> {

    List<MfaRecoveryCode> findByUserMfaIdAndUsedFalse(Long userMfaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT c
                FROM MfaRecoveryCode c
                WHERE c.userMfaId = :mfaId
                  AND c.codeHash = :codeHash
                  AND c.used = false
            """)
    Optional<MfaRecoveryCode> findUnusedForUpdate(
            @Param("mfaId") Long mfaId,
            @Param("codeHash") String codeHash);

    @Modifying
    @Query("""
                DELETE FROM MfaRecoveryCode c
                WHERE c.userMfaId = :mfaId
            """)
    void deleteAllByMfaId(@Param("mfaId") Long mfaId);

}