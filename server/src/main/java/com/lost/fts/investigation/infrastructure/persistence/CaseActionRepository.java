package com.lost.fts.investigation.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseActionRepository extends JpaRepository<CaseActionEntity, Long> {

    List<CaseActionEntity> findByInvestigationCaseIdOrderByPerformedAtAsc(Long caseId);
    List<CaseActionEntity> findByActorIdOrderByPerformedAtDesc(String actorId);
}
