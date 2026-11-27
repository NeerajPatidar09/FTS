package com.lost.fts.rulemanagement.api;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lost.fts.rulemanagement.api.request.RuleVersionCreateRequest;
import com.lost.fts.rulemanagement.api.response.RuleVersionResponse;
import com.lost.fts.rulemanagement.application.RuleVersionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RuleVersionController {

    private final RuleVersionService versionService;

    public RuleVersionController(RuleVersionService versionService) {
        this.versionService = versionService;
    }

    @PostMapping("/rules/{ruleId}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public RuleVersionResponse create(
            @PathVariable Long ruleId,
            @Valid @RequestBody RuleVersionCreateRequest request) {
        return versionService.create(ruleId, request);
    }

    @GetMapping("/rules/{ruleId}/versions")
    public List<RuleVersionResponse> getHistory(
            @PathVariable Long ruleId) {
        return versionService.getHistory(ruleId);
    }

    @GetMapping("/rule-versions/{versionId}")
    public RuleVersionResponse getById(
            @PathVariable Long versionId) {
        return versionService.getById(versionId);
    }

    @PostMapping("/rule-versions/{versionId}/submit")
    public RuleVersionResponse submitForApproval(
            @PathVariable Long versionId) {
        return versionService.submitForApproval(versionId);
    }

    @PostMapping("/rule-versions/{versionId}/approve")
    public RuleVersionResponse approve(
            @PathVariable Long versionId) {
        return versionService.approve(versionId);
    }

    @PostMapping("/rule-versions/{versionId}/reject")
    public RuleVersionResponse reject(
            @PathVariable Long versionId) {
        return versionService.reject(versionId);
    }

    @GetMapping("/rules/code/{ruleCode}/effective-versions")
    public List<RuleVersionResponse> getEffectiveVersions(
            @PathVariable String ruleCode,
            @RequestParam(required = false) Instant at) {
        return versionService.getEffectiveVersions(ruleCode, at);
    }
}