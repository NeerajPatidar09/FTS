package com.lost.fts.riskassessment.application;

import com.lost.fts.riskassessment.infrastructure.persistence.AssessmentOutcome;
import com.lost.fts.riskassessment.infrastructure.persistence.RiskLevel;

public interface RiskDecisionPolicy {

    int calculateRiskScore(Iterable<Integer> ruleScores);
    RiskLevel determineRiskLevel(int riskScore);
    AssessmentOutcome determineOutcome(int riskScore);
    boolean shouldCreateAlert(int riskScore);
}
