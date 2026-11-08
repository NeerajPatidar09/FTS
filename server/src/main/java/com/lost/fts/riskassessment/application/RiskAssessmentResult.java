package com.lost.fts.riskassessment.application;

import java.math.BigDecimal;
import java.util.List;

import com.lost.fts.detection.application.RuleEvaluationResult;

public record RiskAssessmentResult(String transactionRef,BigDecimal riskScore,boolean alertRequired,List<RuleEvaluationResult> ruleResults) {
    public RiskAssessmentResult {
        ruleResults = List.copyOf(ruleResults);
    }
}