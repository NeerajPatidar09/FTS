package com.lost.fts.rulemanagement.api.response;

import java.time.Instant;

import com.lost.fts.rulemanagement.infrastructure.persistence.RuleDomain;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleStatus;

public record RuleResponse(
        Long id,
        String ruleCode,
        String name,
        String description,
        RuleDomain domain,
        RuleStatus status,
        String createdBy,
        Instant createdAt,
        Instant updatedAt,
        Long rowVersion
) {
}