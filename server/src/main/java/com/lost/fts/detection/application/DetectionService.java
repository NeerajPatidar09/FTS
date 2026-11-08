package com.lost.fts.detection.application;

import java.util.List;

public interface DetectionService {
    List<RuleEvaluationResult> evaluate(RuleEvaluationContext context);
}
