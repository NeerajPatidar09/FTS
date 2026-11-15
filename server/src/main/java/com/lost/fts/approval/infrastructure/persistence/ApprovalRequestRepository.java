package com.lost.fts.approval.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    Optional<ApprovalRequest> findByRequestReference(String requestReference);

    List<ApprovalRequest> findByStatusOrderBySubmittedAtAsc(ApprovalStatus status);

    List<ApprovalRequest> findByMakerUserIdOrderBySubmittedAtDesc(Long makerUserId);

    List<ApprovalRequest> findByCheckerUserIdOrderByDecidedAtDesc(Long checkerUserId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT a
                FROM ApprovalRequest a
                WHERE a.id = :id
            """)
    Optional<ApprovalRequest> findByIdForUpdate(@Param("id") Long id);
}