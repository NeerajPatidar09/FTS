package com.lost.fts.rulemanagement.application;

import java.time.Instant;
import java.util.List;

import com.lost.fts.rulemanagement.api.request.RuleVersionCreateRequest;
import com.lost.fts.rulemanagement.api.response.RuleVersionResponse;

public interface RuleVersionService {

    RuleVersionResponse create(Long ruleId, RuleVersionCreateRequest request);

    List<RuleVersionResponse> getHistory(Long ruleId);

    RuleVersionResponse getById(Long versionId);

    RuleVersionResponse submitForApproval(Long versionId);

    RuleVersionResponse approve(Long versionId);

    RuleVersionResponse reject(Long versionId);

    List<RuleVersionResponse> getEffectiveVersions(
            String ruleCode,
            Instant at);
}