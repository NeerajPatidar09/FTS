package com.lost.fts.alert.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lost.fts.alert.infrastructure.persistence.AlertEntity;
import com.lost.fts.alert.infrastructure.persistence.AlertRepository;
import com.lost.fts.alert.infrastructure.persistence.AlertSeverity;
import com.lost.fts.riskassessment.infrastructure.persistence.RiskAssessmentEntity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleDomain;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    @Transactional
    public AlertEntity createAlert( RiskAssessmentEntity assessment, String correlationKey, String deduplicationKey,
            RuleDomain domain, AlertSeverity severity, String title, String description,
            String createdBy, Instant slaDueAt
    ) {
        if (assessment == null) {
            throw new IllegalArgumentException(
                    "Risk assessment is required"
            );
        }

        if (deduplicationKey == null || deduplicationKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Deduplication key is required"
            );
        }

        if (alertRepository.existsByDeduplicationKey(deduplicationKey)) {
            return alertRepository.findByDeduplicationKey(deduplicationKey)
                    .orElseThrow(() -> new IllegalStateException(
                            "Alert exists but could not be retrieved"
                    ));
        }

        AlertEntity alert = new AlertEntity( UUID.randomUUID().toString(), assessment, correlationKey, deduplicationKey, domain, severity, title, description, createdBy, slaDueAt
        );

        return alertRepository.save(alert);
    }
}
