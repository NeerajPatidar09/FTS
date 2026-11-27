package com.lost.fts.rulemanagement.api.request;

import java.time.Instant;

import com.lost.fts.rulemanagement.infrastructure.persistence.RuleSeverity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import tools.jackson.databind.JsonNode;

public record RuleVersionCreateRequest(

        @NotNull
        JsonNode conditionConfig,

        @NotNull
        @Min(0)
        @Max(100)
        Integer riskPoints,

        @NotNull
        RuleSeverity severity,

        @NotNull
        Instant effectiveFrom,

        Instant effectiveTo

) {
}