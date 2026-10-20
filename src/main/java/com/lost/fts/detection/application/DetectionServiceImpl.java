package com.lost.fts.detection.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class DetectionServiceImpl implements DetectionService {

    private final List<RuleEvaluator> evaluators;

    public DetectionServiceImpl(List<RuleEvaluator> evaluators) {
        Set<String> ruleCodes = new HashSet<>();

        for (RuleEvaluator evaluator : evaluators) {
            if (!ruleCodes.add(evaluator.ruleCode())) {
                throw new IllegalStateException("Duplicate rule code: " + evaluator.ruleCode());
            }
        }

        this.evaluators = List.copyOf(evaluators);
    }

    @Override
    public List<RuleEvaluationResult> evaluate(
            RuleEvaluationContext context) {

        return evaluators.stream().map(evaluator -> evaluator.evaluate(context)).toList();
    }
}
