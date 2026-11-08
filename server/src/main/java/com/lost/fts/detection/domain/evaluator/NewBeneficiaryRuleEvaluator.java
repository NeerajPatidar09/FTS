package com.lost.fts.detection.domain.evaluator;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.lost.fts.detection.application.RuleEvaluationContext;
import com.lost.fts.detection.application.RuleEvaluationResult;
import com.lost.fts.detection.application.RuleEvaluator;

@Component
public class NewBeneficiaryRuleEvaluator implements RuleEvaluator {

    private static final String RULECODE = "NEWBENEFICIARYTRANSFER";

    private static final Duration NEWBENEFICIARYWINDOW =
            Duration.ofHours(24);

    private static final BigDecimal MINIMUMAMOUNT =
            new BigDecimal("10000.00");

    @Override
    public String ruleCode() {
        return RULECODE;
    }

    @Override
    public RuleEvaluationResult evaluate(
            RuleEvaluationContext context) {

        BigDecimal amount = context.transaction().getAmount();

        if (amount.compareTo(MINIMUMAMOUNT) < 0
                || !context.hasNewBeneficiary(NEWBENEFICIARYWINDOW)) {
            return RuleEvaluationResult.noMatch(RULECODE);
        }

        return new RuleEvaluationResult(
                RULECODE,
                true,
                BigDecimal.valueOf(40),
                "A qualifying transfer involves a recently created beneficiary",
                Map.of(
                        "amount", amount,
                        "minimumAmount", MINIMUMAMOUNT,
                        "beneficiaryAgeWindowHours", 24
                )
        );
    }
}