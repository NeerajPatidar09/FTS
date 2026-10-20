package com.lost.fts.detection.application;

public interface RuleEvaluator {

    String ruleCode();
    RuleEvaluationResult evaluate(RuleEvaluationContext context);
}