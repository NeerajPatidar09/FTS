package com.lost.fts.detection.domain.evaluator;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.lost.fts.detection.application.RuleEvaluationContext;
import com.lost.fts.detection.application.RuleEvaluationResult;
import com.lost.fts.detection.application.RuleEvaluator;

@Component
public class LargeTransactionRuleEvaluator implements RuleEvaluator {

    private static final String RULECODE = "LARGETRANSACTION";

    private static final BigDecimal THRESHOLD =
            new BigDecimal("50000.00");

    @Override
    public String ruleCode() {
        return RULECODE;
    }

    @Override
    public RuleEvaluationResult evaluate(
            RuleEvaluationContext context) {

        BigDecimal amount = context.transaction().getAmount();

        if (amount.compareTo(THRESHOLD) <= 0) {
            return RuleEvaluationResult.noMatch(RULECODE);
        }

        return new RuleEvaluationResult(
                RULECODE,
                true,
                BigDecimal.valueOf(35),
                "Transaction amount exceeds the configured threshold",
                Map.of(
                        "amount", amount,
                        "threshold", THRESHOLD,
                        "currency", context.transaction().getCurrency()
                )
        );
    }
}
