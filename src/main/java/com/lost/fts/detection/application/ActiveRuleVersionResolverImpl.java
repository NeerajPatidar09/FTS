package com.lost.fts.detection.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionEntity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionRepository;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionStatus;

@Component
public class ActiveRuleVersionResolverImpl implements ActiveRuleVersionResolver {

    private final RuleVersionRepository repository;

    public ActiveRuleVersionResolverImpl(RuleVersionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RuleVersionEntity> findActiveVersion(String ruleCode) {
        List<RuleVersionEntity> versions =
                repository.findEffectiveVersions(
                        ruleCode,
                        RuleVersionStatus.APPROVED,
                        Instant.now()
                );

        if (versions.size() > 1) {
            throw new IllegalStateException(
                    "Multiple effective approved versions for rule: "
                            + ruleCode
            );
        }

        return versions.stream().findFirst();
    }
}
