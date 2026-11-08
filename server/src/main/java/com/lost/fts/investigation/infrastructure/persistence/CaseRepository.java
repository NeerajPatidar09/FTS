package com.lost.fts.investigation.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {

    Optional<CaseEntity> findByCaseRef(String caseRef);

    List<CaseEntity> findByAssignedToAndStatus(String assignedTo, CaseStatus status);

    List<CaseEntity> findByStatusAndPriority(CaseStatus status, CasePriority priority);
}
