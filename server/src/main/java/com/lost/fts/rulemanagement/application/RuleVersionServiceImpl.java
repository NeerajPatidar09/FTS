package com.lost.fts.rulemanagement.application;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lost.fts.rulemanagement.api.request.RuleVersionCreateRequest;
import com.lost.fts.rulemanagement.api.response.RuleVersionResponse;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleEntity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleRepository;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleStatus;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionEntity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionRepository;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionStatus;

@Service
@Transactional
public class RuleVersionServiceImpl implements RuleVersionService {

    private final RuleRepository ruleRepository;
    private final RuleVersionRepository versionRepository;
    private final RuleVersionMapper mapper;

    public RuleVersionServiceImpl(
            RuleRepository ruleRepository,
            RuleVersionRepository versionRepository,
            RuleVersionMapper mapper) {
        this.ruleRepository = ruleRepository;
        this.versionRepository = versionRepository;
        this.mapper = mapper;
    }

    @Override
    public RuleVersionResponse create(
            Long ruleId,
            RuleVersionCreateRequest request) {

        RuleEntity rule = findRule(ruleId);

        if (rule.getStatus() == RuleStatus.RETIRED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot create a version for a retired rule");
        }

        if (request.conditionConfig() == null
                || !request.conditionConfig().isObject()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Condition configuration must be a JSON object");
        }

        if (request.effectiveTo() != null
                && !request.effectiveTo().isAfter(request.effectiveFrom())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Effective-to must be after effective-from");
        }

        Integer nextVersion = versionRepository
                .findTopByRuleIdOrderByVersionNumberDesc(ruleId)
                .map(existing -> existing.getVersionNumber() + 1)
                .orElse(1);

        RuleVersionEntity version = new RuleVersionEntity(
                rule,
                nextVersion,
                request.conditionConfig(),
                request.riskPoints(),
                request.severity(),
                request.effectiveFrom(),
                request.effectiveTo(),
                currentActor());

        return mapper.toResponse(versionRepository.save(version));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleVersionResponse> getHistory(Long ruleId) {

        findRule(ruleId);

        return versionRepository
                .findByRuleIdOrderByVersionNumberDesc(ruleId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RuleVersionResponse getById(Long versionId) {
        return mapper.toResponse(findVersion(versionId));
    }

    @Override
    public RuleVersionResponse submitForApproval(Long versionId) {

        RuleVersionEntity version = findVersion(versionId);

        try {
            version.submitForApproval();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage());
        }

        return mapper.toResponse(versionRepository.save(version));
    }

    @Override
    public RuleVersionResponse approve(Long versionId) {

        RuleVersionEntity version = findVersion(versionId);

        try {
            version.approve(currentActor(), Instant.now());
        } catch (IllegalStateException | IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage());
        }

        return mapper.toResponse(versionRepository.save(version));
    }

    @Override
    public RuleVersionResponse reject(Long versionId) {

        RuleVersionEntity version = findVersion(versionId);

        try {
            version.reject();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage());
        }

        return mapper.toResponse(versionRepository.save(version));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleVersionResponse> getEffectiveVersions(
            String ruleCode,
            Instant at) {

        if (at == null) {
            at = Instant.now();
        }

        return versionRepository.findEffectiveVersions(
                        ruleCode,
                        RuleVersionStatus.APPROVED,
                        at)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    private RuleEntity findRule(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rule not found"));
    }

    private RuleVersionEntity findVersion(Long id) {
        return versionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rule version not found"));
    }

    private String currentActor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user is required");
        }

        String actor = authentication.getName();

        if (actor == null || actor.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated username is unavailable");
        }

        return actor.trim();
    }
}