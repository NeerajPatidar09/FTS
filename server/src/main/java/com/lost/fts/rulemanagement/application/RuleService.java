package com.lost.fts.rulemanagement.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lost.fts.rulemanagement.api.request.RuleCreateRequest;
import com.lost.fts.rulemanagement.api.request.RuleUpdateRequest;
import com.lost.fts.rulemanagement.api.response.RuleResponse;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleDomain;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleStatus;

public interface RuleService {

    RuleResponse create(RuleCreateRequest request);

    RuleResponse getById(Long id);

    RuleResponse getByRuleCode(String ruleCode);

    Page<RuleResponse> getAll(
            RuleDomain domain,
            RuleStatus status,
            String search,
            Pageable pageable);

    RuleResponse update(Long id, RuleUpdateRequest request);

    RuleResponse enable(Long id);

    RuleResponse disable(Long id);

    RuleResponse retire(Long id);
}