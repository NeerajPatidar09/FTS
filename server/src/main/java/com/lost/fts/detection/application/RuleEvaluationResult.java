package com.lost.fts.detection.application;

import java.math.BigDecimal;
import java.util.Map;

public record RuleEvaluationResult(
        String ruleCode, boolean matched, BigDecimal riskScore, String reason, Map<String, Object> evidence) {
    public RuleEvaluationResult {
        if (ruleCode == null || ruleCode.isBlank()) {
            throw new IllegalArgumentException("Rule code is required");
        }

        if (riskScore == null
                || riskScore.compareTo(BigDecimal.ZERO) < 0
                || riskScore.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(
                    "Risk score must be between 0 and 100");
        }

        if (matched && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException(
                    "A matched rule requires a reason");
        }

        evidence = evidence == null ? Map.of() : Map.copyOf(evidence);
    }

    public static RuleEvaluationResult noMatch(String ruleCode) {
        return new RuleEvaluationResult(
                ruleCode,
                false,
                BigDecimal.ZERO,
                null,
                Map.of());
    }
}