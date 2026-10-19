package com.lost.fts.rulemanagement.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleRepository extends JpaRepository<RuleEntity, Long> {

    Optional<RuleEntity> findByRuleCode(String ruleCode);

    List<RuleEntity> findByDomainAndStatus(RuleDomain domain, RuleStatus status);
}
