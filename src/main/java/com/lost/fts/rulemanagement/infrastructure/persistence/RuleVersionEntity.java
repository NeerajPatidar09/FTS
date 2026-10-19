package com.lost.fts.rulemanagement.infrastructure.persistence;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "ruleversions", uniqueConstraints = {
        @UniqueConstraint(name = "ukruleversionsruleversion", columnNames = { "ruleid", "versionnumber" })
}, indexes = {
        @Index(name = "idxruleversionsstatuseffective", columnList = "status, effectivefrom, effectiveto")
})
public class RuleVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ruleid", nullable = false, foreignKey = @ForeignKey(name = "fkruleversionsrule"))
    private RuleEntity rule;

    @Column(name = "versionnumber", nullable = false)
    private Integer versionNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conditionconfig", nullable = false, columnDefinition = "json")
    private JsonNode conditionConfig;

    @Column(name = "riskpoints", nullable = false)
    private Integer riskPoints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RuleSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RuleVersionStatus status = RuleVersionStatus.DRAFT;

    @Column(name = "effectivefrom", nullable = false)
    private Instant effectiveFrom;

    @Column(name = "effectiveto")
    private Instant effectiveTo;

    @Column(name = "createdby", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "approvedby", length = 100)
    private String approvedBy;

    @Column(name = "approvedat")
    private Instant approvedAt;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    protected RuleVersionEntity() {
        // Required by JPA
    }

    public RuleVersionEntity(
            RuleEntity rule,
            Integer versionNumber,
            JsonNode conditionConfig,
            Integer riskPoints,
            RuleSeverity severity,
            Instant effectiveFrom,
            Instant effectiveTo,
            String createdBy) {

        if (rule == null) {
            throw new IllegalArgumentException("Rule is required");
        }

        if (versionNumber == null || versionNumber < 1) {
            throw new IllegalArgumentException(
                "Version number must be positive"
            );
        }

        if (conditionConfig == null || !conditionConfig.isObject()) {
            throw new IllegalArgumentException(
                "Condition configuration must be a JSON object"
            );
        }

        if (riskPoints == null || riskPoints < 0 || riskPoints > 100) {
            throw new IllegalArgumentException(
                "Risk points must be between 0 and 100"
            );
        }

        if (severity == null || effectiveFrom == null) {
            throw new IllegalArgumentException(
                "Severity and effective-from time are required"
            );
        }

        if (effectiveTo != null && !effectiveTo.isAfter(effectiveFrom)) {
            throw new IllegalArgumentException(
                "Effective-to time must be after effective-from time"
            );
        }

        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException(
                "Creator is required"
            );
        }

        this.rule = rule;
        this.versionNumber = versionNumber;
        this.conditionConfig = conditionConfig.deepCopy();
        this.riskPoints = riskPoints;
        this.severity = severity;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.createdBy = createdBy.trim();
        this.status = RuleVersionStatus.DRAFT;
    }

    public void submitForApproval() {
        if (status != RuleVersionStatus.DRAFT) {
            throw new IllegalStateException(
                "Only draft versions can be submitted"
            );
        }

        status = RuleVersionStatus.PENDINGAPPROVAL;
    }

    public void approve(String approver, Instant approvedAt) {
        if (status != RuleVersionStatus.PENDINGAPPROVAL) {
            throw new IllegalStateException(
                "Only pending versions can be approved"
            );
        }

        if (approver == null || approver.isBlank()
                || approver.trim().equals(createdBy)) {
            throw new IllegalArgumentException(
                "A different, identified approver is required"
            );
        }

        if (approvedAt == null) {
            throw new IllegalArgumentException(
                "Approval timestamp is required"
            );
        }

        this.approvedBy = approver.trim();
        this.approvedAt = approvedAt;
        this.status = RuleVersionStatus.APPROVED;
    }

    public void reject() {
        if (status != RuleVersionStatus.PENDINGAPPROVAL) {
            throw new IllegalStateException(
                "Only pending versions can be rejected"
            );
        }

        status = RuleVersionStatus.REJECTED;
    }

    public void supersede(Instant endTime) {
        if (status != RuleVersionStatus.APPROVED) {
            throw new IllegalStateException(
                "Only approved versions can be superseded"
            );
        }

        if (endTime == null || !endTime.isAfter(effectiveFrom)) {
            throw new IllegalArgumentException(
                "End time must be after the effective-from time"
            );
        }

        if (effectiveTo != null && endTime.isAfter(effectiveTo)) {
            throw new IllegalArgumentException(
                "End time cannot extend the existing effective range"
            );
        }

        this.effectiveTo = endTime;
        this.status = RuleVersionStatus.SUPERSEDED;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public RuleEntity getRule() { return rule; }
    public Integer getVersionNumber() { return versionNumber; }

    public JsonNode getConditionConfig() {
        return conditionConfig == null
                ? null
                : conditionConfig.deepCopy();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRule(RuleEntity rule) {
        this.rule = rule;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public void setConditionConfig(JsonNode conditionConfig) {
        this.conditionConfig = conditionConfig;
    }

    public Integer getRiskPoints() {
        return riskPoints;
    }

    public void setRiskPoints(Integer riskPoints) {
        this.riskPoints = riskPoints;
    }

    public RuleSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(RuleSeverity severity) {
        this.severity = severity;
    }

    public RuleVersionStatus getStatus() {
        return status;
    }

    public void setStatus(RuleVersionStatus status) {
        this.status = status;
    }

    public Instant getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Instant effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Instant getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(Instant effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    } 

}
