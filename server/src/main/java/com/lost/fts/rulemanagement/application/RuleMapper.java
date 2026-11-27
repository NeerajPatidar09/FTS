package com.lost.fts.rulemanagement.application;

import org.springframework.stereotype.Component;

import com.lost.fts.rulemanagement.api.response.RuleResponse;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleEntity;

@Component
public class RuleMapper {

    public RuleResponse toResponse(RuleEntity rule) {
        return new RuleResponse(
                rule.getId(),
                rule.getRuleCode(),
                rule.getName(),
                rule.getDescription(),
                rule.getDomain(),
                rule.getStatus(),
                rule.getCreatedBy(),
                rule.getCreatedAt(),
                rule.getUpdatedAt(),
                rule.getRowVersion()
        );
    }
}