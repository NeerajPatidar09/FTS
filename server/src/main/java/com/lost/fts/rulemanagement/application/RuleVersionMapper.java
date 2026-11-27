package com.lost.fts.rulemanagement.application;

import org.springframework.stereotype.Component;

import com.lost.fts.rulemanagement.api.response.RuleVersionResponse;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionEntity;

@Component
public class RuleVersionMapper {

    public RuleVersionResponse toResponse(RuleVersionEntity version) {
        return new RuleVersionResponse(
                version.getId(),
                version.getRule().getId(),
                version.getRule().getRuleCode(),
                version.getVersionNumber(),
                version.getConditionConfig(),
                version.getRiskPoints(),
                version.getSeverity(),
                version.getStatus(),
                version.getEffectiveFrom(),
                version.getEffectiveTo(),
                version.getCreatedBy(),
                version.getApprovedBy(),
                version.getApprovedAt(),
                version.getCreatedAt()
        );
    }
}