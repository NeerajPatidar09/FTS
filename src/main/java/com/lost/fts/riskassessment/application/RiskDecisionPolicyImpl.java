package com.lost.fts.riskassessment.application;

import org.springframework.stereotype.Component;

import com.lost.fts.riskassessment.infrastructure.persistence.AssessmentOutcome;
import com.lost.fts.riskassessment.infrastructure.persistence.RiskLevel;

@Component
public class RiskDecisionPolicyImpl implements RiskDecisionPolicy {

    private static final int MEDIUMTHRESHOLD = 25;
    private static final int HIGHTHRESHOLD = 50;
    private static final int CRITICALTHRESHOLD = 75;

    private static final int MANUALREVIEWTHRESHOLD = 50;
    private static final int ALERTTHRESHOLD = 70;

    @Override
    public int calculateRiskScore(Iterable<Integer> ruleScores) {
        int maxScore = 0;

        for (Integer score : ruleScores) {
            if (score == null || score < 0 || score > 100) {
                throw new IllegalArgumentException(
                        "Rule scores must be between 0 and 100");
            }

            maxScore = Math.max(maxScore, score);
        }

        return maxScore;
    }

    @Override
    public RiskLevel determineRiskLevel(int riskScore) {
        validateScore(riskScore);

        if (riskScore >= CRITICALTHRESHOLD) {
            return RiskLevel.CRITICAL;
        }
        if (riskScore >= HIGHTHRESHOLD) {
            return RiskLevel.HIGH;
        }
        if (riskScore >= MEDIUMTHRESHOLD) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.LOW;
    }

    @Override
    public AssessmentOutcome determineOutcome(int riskScore) {
        validateScore(riskScore);

        if (riskScore >= ALERTTHRESHOLD) {
            return AssessmentOutcome.ALERTRECOMMENDED;
        }
        if (riskScore >= MANUALREVIEWTHRESHOLD) {
            return AssessmentOutcome.MANUALREVIEW;
        }

        return AssessmentOutcome.NOALERT;
    }

    @Override
    public boolean shouldCreateAlert(int riskScore) {
        validateScore(riskScore);
        return riskScore >= ALERTTHRESHOLD;
    }

    private void validateScore(int riskScore) {
        if (riskScore < 0 || riskScore > 100) {
            throw new IllegalArgumentException(
                    "Risk score must be between 0 and 100");
        }
    }
}