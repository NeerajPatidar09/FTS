package com.lost.fts.rulemanagement.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RuleVersionRepository extends JpaRepository<RuleVersionEntity, Long> {

    Optional<RuleVersionEntity> findByRuleIdAndVersionNumber(Long ruleId, Integer versionNumber);

    List<RuleVersionEntity> findByRuleIdOrderByVersionNumberDesc(Long ruleId);

    List<RuleVersionEntity> findByRuleIdAndStatus(Long ruleId, RuleVersionStatus status);

    @Query("""
      select rv from RuleVersionEntity rv join rv.rule r where r.ruleCode = :ruleCode 
      and rv.status = :status and rv.effectiveFrom <= :at and (rv.effectiveTo is null or rv.effectiveTo > :at) order by rv.versionNumber desc """)
    List<RuleVersionEntity> findEffectiveVersions(@Param("ruleCode") String ruleCode, @Param("status") RuleVersionStatus status, @Param("at") Instant at );
}
