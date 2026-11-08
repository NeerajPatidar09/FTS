package com.lost.fts.alert.application;

import java.time.Instant;

import com.lost.fts.alert.infrastructure.persistence.AlertEntity;
import com.lost.fts.alert.infrastructure.persistence.AlertSeverity;
import com.lost.fts.riskassessment.infrastructure.persistence.RiskAssessmentEntity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleDomain;

public interface AlertService {
    AlertEntity createAlert( RiskAssessmentEntity assessment, String correlationKey, String deduplicationKey, RuleDomain domain, AlertSeverity severity, String title, String description, String createdBy, Instant slaDueAt);
}
