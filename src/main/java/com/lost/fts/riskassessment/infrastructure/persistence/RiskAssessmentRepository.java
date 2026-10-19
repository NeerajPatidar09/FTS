package com.lost.fts.riskassessment.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessmentEntity, Long> {

    Optional<RiskAssessmentEntity> findByAssessmentRef(String assessmentRef);

    List<RiskAssessmentEntity> findByTransactionIdOrderByCreatedAtDesc(Long transactionId);
}
