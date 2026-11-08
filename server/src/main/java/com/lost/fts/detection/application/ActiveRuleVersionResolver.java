package com.lost.fts.detection.application;

import java.util.Optional;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionEntity;

public interface ActiveRuleVersionResolver {
    Optional<RuleVersionEntity> findActiveVersion(String ruleCode);
}
