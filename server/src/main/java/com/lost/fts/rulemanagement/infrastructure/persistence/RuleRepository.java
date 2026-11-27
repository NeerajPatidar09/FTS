package com.lost.fts.rulemanagement.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleRepository extends JpaRepository<RuleEntity, Long> {

    Optional<RuleEntity> findByRuleCode(String ruleCode);

    boolean existsByRuleCode(String ruleCode);

    boolean existsByRuleCodeAndIdNot(String ruleCode, Long id);

    Page<RuleEntity> findByDomain(RuleDomain domain, Pageable pageable);

    Page<RuleEntity> findByStatus(RuleStatus status, Pageable pageable);

    Page<RuleEntity> findByDomainAndStatus(
            RuleDomain domain,
            RuleStatus status,
            Pageable pageable);

    Page<RuleEntity> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable);

    Page<RuleEntity> findByRuleCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
            String ruleCode,
            String name,
            Pageable pageable);
}