package com.lost.fts.rulemanagement.api.response;

import java.time.Instant;

import com.lost.fts.rulemanagement.infrastructure.persistence.RuleSeverity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionStatus;

import tools.jackson.databind.JsonNode;

public record RuleVersionResponse(
        Long id,
        Long ruleId,
        String ruleCode,
        Integer versionNumber,
        JsonNode conditionConfig,
        Integer riskPoints,
        RuleSeverity severity,
        RuleVersionStatus status,
        Instant effectiveFrom,
        Instant effectiveTo,
        String createdBy,
        String approvedBy,
        Instant approvedAt,
        Instant createdAt
) {
}