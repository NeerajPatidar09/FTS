package com.lost.fts.investigation.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseNoteRepository extends JpaRepository<CaseNoteEntity, Long> {

    List<CaseNoteEntity> findByInvestigationCaseIdOrderByCreatedAtAsc(Long caseId);
}
