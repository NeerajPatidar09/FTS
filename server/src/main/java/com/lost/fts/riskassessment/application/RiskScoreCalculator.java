package com.lost.fts.riskassessment.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.lost.fts.detection.application.RuleEvaluationResult;

@Component
public class RiskScoreCalculator {

    public BigDecimal calculate(List<RuleEvaluationResult> results) {
        return results.stream()
                .filter(RuleEvaluationResult::matched)
                .map(RuleEvaluationResult::riskScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
