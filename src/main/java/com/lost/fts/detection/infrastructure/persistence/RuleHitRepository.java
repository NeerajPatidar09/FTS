package com.lost.fts.detection.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleHitRepository extends JpaRepository<RuleHitEntity, Long> {

    List<RuleHitEntity> findByRiskAssessmentId(Long assessmentId);

    boolean existsByRiskAssessmentIdAndRuleVersionId(Long assessmentId, Long ruleVersionId);
}
