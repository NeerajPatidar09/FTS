package com.lost.fts.detection.domain.evaluator;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.lost.fts.detection.application.RuleEvaluationContext;
import com.lost.fts.detection.application.RuleEvaluationResult;
import com.lost.fts.detection.application.RuleEvaluator;

@Component
public class VelocityRuleEvaluator implements RuleEvaluator {

    private static final String RULECODE = "HIGHTRANSACTIONVELOCITY";
    private static final long MAX_TRANSACTIONS = 5;

    @Override
    public String ruleCode() {
        return RULECODE;
    }

    @Override
    public RuleEvaluationResult evaluate(
            RuleEvaluationContext context) {

        long count = context.recentTransactionCount();

        if (count <= MAX_TRANSACTIONS) {
            return RuleEvaluationResult.noMatch(RULECODE);
        }

        return new RuleEvaluationResult(
                RULECODE,
                true,
                BigDecimal.valueOf(30),
                "Recent transaction count exceeds the configured limit",
                Map.of(
                        "recentTransactionCount", count,
                        "maximumAllowed", MAX_TRANSACTIONS
                )
        );
    }
}