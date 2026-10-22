package com.lost.fts.riskassessment.application;

import com.lost.fts.detection.application.RuleEvaluationContext;

public interface RiskAssessmentService {
    RiskAssessmentResult assess(RuleEvaluationContext context);
}
