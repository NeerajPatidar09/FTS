package com.lost.fts.rulemanagement.application;

import java.time.Instant;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lost.fts.rulemanagement.api.request.RuleCreateRequest;
import com.lost.fts.rulemanagement.api.request.RuleUpdateRequest;
import com.lost.fts.rulemanagement.api.response.RuleResponse;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleEntity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleDomain;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleRepository;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleStatus;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionRepository;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionStatus;

@Service
@Transactional
public class RuleServiceImpl implements RuleService {

    private final RuleRepository ruleRepository;
    private final RuleVersionRepository versionRepository;
    private final RuleMapper ruleMapper;

    public RuleServiceImpl(
            RuleRepository ruleRepository,
            RuleVersionRepository versionRepository,
            RuleMapper ruleMapper) {
        this.ruleRepository = ruleRepository;
        this.versionRepository = versionRepository;
        this.ruleMapper = ruleMapper;
    }

    @Override
    public RuleResponse create(RuleCreateRequest request) {

        String ruleCode = request.ruleCode().trim().toUpperCase(Locale.ROOT);

        if (ruleRepository.existsByRuleCode(ruleCode)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Rule code already exists");
        }

        Instant now = Instant.now();

        RuleEntity rule = new RuleEntity();
        rule.setRuleCode(ruleCode);
        rule.setName(request.name().trim());
        rule.setDescription(normalize(request.description()));
        rule.setDomain(request.domain());
        rule.setStatus(RuleStatus.DISABLED);
        rule.setCreatedBy(currentActor());
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);

        return ruleMapper.toResponse(ruleRepository.save(rule));
    }

    @Override
    @Transactional(readOnly = true)
    public RuleResponse getById(Long id) {
        return ruleMapper.toResponse(findRule(id));
    }

    @Override
    @Transactional(readOnly = true)
    public RuleResponse getByRuleCode(String ruleCode) {
        RuleEntity rule = ruleRepository.findByRuleCode(
                ruleCode.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rule not found"));

        return ruleMapper.toResponse(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RuleResponse> getAll(
            RuleDomain domain,
            RuleStatus status,
            String search,
            Pageable pageable) {

        boolean hasSearch = search != null && !search.isBlank();

        Page<RuleEntity> result;

        if (domain != null && status != null) {
            result = ruleRepository.findByDomainAndStatus(
                    domain, status, pageable);
        } else if (domain != null) {
            result = ruleRepository.findByDomain(domain, pageable);
        } else if (status != null) {
            result = ruleRepository.findByStatus(status, pageable);
        } else if (hasSearch) {
            String term = search.trim();
            result = ruleRepository
                    .findByRuleCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
                            term, term, pageable);
        } else {
            result = ruleRepository.findAll(pageable);
        }

        return result.map(ruleMapper::toResponse);
    }

    @Override
    public RuleResponse update(Long id, RuleUpdateRequest request) {

        RuleEntity rule = findRule(id);

        if (rule.getStatus() == RuleStatus.RETIRED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Retired rules cannot be updated");
        }

        if (rule.getStatus() == RuleStatus.ENABLED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Disable the rule before updating its metadata");
        }

        rule.setName(request.name().trim());
        rule.setDescription(normalize(request.description()));
        rule.setDomain(request.domain());
        rule.setUpdatedAt(Instant.now());

        return ruleMapper.toResponse(ruleRepository.save(rule));
    }

    @Override
    public RuleResponse enable(Long id) {

        RuleEntity rule = findRule(id);

        if (rule.getStatus() == RuleStatus.RETIRED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Retired rules cannot be enabled");
        }

        if (rule.getStatus() == RuleStatus.ENABLED) {
            return ruleMapper.toResponse(rule);
        }

        Instant now = Instant.now();

        boolean hasEffectiveApprovedVersion =
                versionRepository.findEffectiveVersions(
                        rule.getRuleCode(),
                        RuleVersionStatus.APPROVED,
                        now).stream().findAny().isPresent();

        if (!hasEffectiveApprovedVersion) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Rule requires an approved, currently effective version");
        }

        rule.setStatus(RuleStatus.ENABLED);
        rule.setUpdatedAt(now);

        return ruleMapper.toResponse(ruleRepository.save(rule));
    }

    @Override
    public RuleResponse disable(Long id) {

        RuleEntity rule = findRule(id);

        if (rule.getStatus() == RuleStatus.RETIRED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Retired rules cannot be changed");
        }

        rule.setStatus(RuleStatus.DISABLED);
        rule.setUpdatedAt(Instant.now());

        return ruleMapper.toResponse(ruleRepository.save(rule));
    }

    @Override
    public RuleResponse retire(Long id) {

        RuleEntity rule = findRule(id);

        rule.setStatus(RuleStatus.RETIRED);
        rule.setUpdatedAt(Instant.now());

        return ruleMapper.toResponse(ruleRepository.save(rule));
    }

    private RuleEntity findRule(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rule not found"));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
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