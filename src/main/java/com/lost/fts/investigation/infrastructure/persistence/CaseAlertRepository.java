package com.lost.fts.investigation.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseAlertRepository extends JpaRepository<CaseAlertEntity, Long> {

    List<CaseAlertEntity> findByInvestigationCaseId(Long caseId);

    boolean existsByInvestigationCaseIdAndAlertId(Long caseId, Long alertId);
}
