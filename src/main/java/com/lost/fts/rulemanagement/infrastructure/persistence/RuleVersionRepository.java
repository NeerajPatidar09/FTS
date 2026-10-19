package com.lost.fts.rulemanagement.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleVersionRepository extends JpaRepository<RuleVersionEntity, Long> {

    Optional<RuleVersionEntity> findByRuleIdAndVersionNumber(Long ruleId, Integer versionNumber);

    List<RuleVersionEntity> findByRuleIdOrderByVersionNumberDesc(Long ruleId);

    List<RuleVersionEntity> findByRuleIdAndStatus(Long ruleId, RuleVersionStatus status);
}
