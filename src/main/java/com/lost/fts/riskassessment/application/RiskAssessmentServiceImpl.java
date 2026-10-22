package com.lost.fts.riskassessment.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lost.fts.detection.application.DetectionService;
import com.lost.fts.detection.application.RuleEvaluationContext;
import com.lost.fts.detection.application.RuleEvaluationResult;

@Service
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private final DetectionService detectionService;
    private final RiskScoreCalculator scoreCalculator;
    private final BigDecimal alertThreshold;

    public RiskAssessmentServiceImpl(DetectionService detectionService, RiskScoreCalculator scoreCalculator,
            @Value("${ews.risk.alert-threshold:70}") BigDecimal alertThreshold) {
        this.detectionService = detectionService;
        this.scoreCalculator = scoreCalculator;
        this.alertThreshold = alertThreshold;

        if (alertThreshold.compareTo(BigDecimal.ZERO) < 0
                || alertThreshold.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Alert threshold must be between 0 and 100");
        }
    }

    @Override
    @Transactional
    public RiskAssessmentResult assess(RuleEvaluationContext context) {
        List<RuleEvaluationResult> results = detectionService.evaluate(context);

        BigDecimal riskScore = scoreCalculator.calculate(results);
        boolean alertRequired = riskScore.compareTo(alertThreshold) >= 0;
        String transactionRef = context.transaction().getTransactionRef();
        return new RiskAssessmentResult(transactionRef, riskScore, alertRequired, results);
    }
}
