package com.lost.fts.rulemanagement.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lost.fts.rulemanagement.api.request.RuleCreateRequest;
import com.lost.fts.rulemanagement.api.request.RuleUpdateRequest;
import com.lost.fts.rulemanagement.api.response.RuleResponse;
import com.lost.fts.rulemanagement.application.RuleService;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleDomain;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RuleResponse create(
            @Valid @RequestBody RuleCreateRequest request) {
        return ruleService.create(request);
    }

    @GetMapping("/{id}")
    public RuleResponse getById(@PathVariable Long id) {
        return ruleService.getById(id);
    }

    @GetMapping("/code/{ruleCode}")
    public RuleResponse getByRuleCode(@PathVariable String ruleCode) {
        return ruleService.getByRuleCode(ruleCode);
    }

    @GetMapping
    public Page<RuleResponse> getAll(
            @RequestParam(required = false) RuleDomain domain,
            @RequestParam(required = false) RuleStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable) {

        return ruleService.getAll(domain, status, search, pageable);
    }

    @PutMapping("/{id}")
    public RuleResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RuleUpdateRequest request) {
        return ruleService.update(id, request);
    }

    @PatchMapping("/{id}/enable")
    public RuleResponse enable(@PathVariable Long id) {
        return ruleService.enable(id);
    }

    @PatchMapping("/{id}/disable")
    public RuleResponse disable(@PathVariable Long id) {
        return ruleService.disable(id);
    }

    @PatchMapping("/{id}/retire")
    public RuleResponse retire(@PathVariable Long id) {
        return ruleService.retire(id);
    }
}