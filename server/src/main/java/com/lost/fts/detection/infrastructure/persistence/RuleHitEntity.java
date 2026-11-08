package com.lost.fts.detection.infrastructure.persistence;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.lost.fts.riskassessment.infrastructure.persistence.AssessmentStatus;
import com.lost.fts.riskassessment.infrastructure.persistence.RiskAssessmentEntity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleSeverity;
import com.lost.fts.rulemanagement.infrastructure.persistence.RuleVersionEntity;

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
@Table(name = "rulehits", uniqueConstraints = {
        @UniqueConstraint(name = "ukrulehitsassessmentversion", columnNames = { "riskassessmentid",
                "ruleversionid" }),
        @UniqueConstraint(name = "ukrulehitsref", columnNames = "rulehitref")
}, indexes = {
        @Index(name = "idxrulehitsruleversion", columnList = "ruleversionid")
})
public class RuleHitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rulehitref", nullable = false, length = 64)
    private String ruleHitRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "riskassessmentid", nullable = false, foreignKey = @ForeignKey(name = "fkrulehitsassessment"))
    private RiskAssessmentEntity riskAssessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ruleversionid", nullable = false, foreignKey = @ForeignKey(name = "fkrulehitsruleversion"))
    private RuleVersionEntity ruleVersion;

    // Snapshot values preserve the identity used during evaluation.
    @Column(name = "rulecodesnapshot", nullable = false, length = 100)
    private String ruleCodeSnapshot;

    @Column(name = "ruleversionnumbersnapshot", nullable = false)
    private Integer ruleVersionNumberSnapshot;

    @Column(name = "riskpoints", nullable = false)
    private Integer riskPoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "severitysnapshot", nullable = false, length = 20)
    private RuleSeverity severitySnapshot;

    @Column(name = "reasoncode", nullable = false, length = 100)
    private String reasonCode;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    // Store concise evidence references/facts, not raw sensitive data.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evidence", columnDefinition = "json")
    private JsonNode evidence;

    @Column(name = "triggeredat", nullable = false)
    private Instant triggeredAt;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    protected RuleHitEntity() {
        // Required by JPA
    }

    public RuleHitEntity(
            String ruleHitRef,
            RiskAssessmentEntity riskAssessment,
            RuleVersionEntity ruleVersion,
            int riskPoints,
            String reasonCode,
            String reason,
            JsonNode evidence,
            Instant triggeredAt) {

        if (ruleHitRef == null || ruleHitRef.isBlank()) {
            throw new IllegalArgumentException(
                    "Rule-hit reference is required");
        }

        if (riskAssessment == null || ruleVersion == null) {
            throw new IllegalArgumentException(
                    "Assessment and rule version are required");
        }

        if (riskAssessment
                .getStatus() != AssessmentStatus.PROCESSING) {
            throw new IllegalArgumentException(
                    "Rule hits must be recorded while assessment is processing");
        }

        if (riskPoints < 0 || riskPoints > 100) {
            throw new IllegalArgumentException(
                    "Risk points must be between 0 and 100");
        }

        if (reasonCode == null || reasonCode.isBlank()
                || reason == null || reason.isBlank()
                || triggeredAt == null) {
            throw new IllegalArgumentException(
                    "Reason code, reason and trigger time are required");
        }

        this.ruleHitRef = ruleHitRef.trim();
        this.riskAssessment = riskAssessment;
        this.ruleVersion = ruleVersion;
        this.ruleCodeSnapshot = ruleVersion.getRule().getRuleCode();
        this.ruleVersionNumberSnapshot = ruleVersion.getVersionNumber();
        this.riskPoints = riskPoints;
        this.severitySnapshot = ruleVersion.getSeverity();
        this.reasonCode = reasonCode.trim();
        this.reason = reason.trim();
        this.evidence = evidence == null ? null : evidence.deepCopy();
        this.triggeredAt = triggeredAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleHitRef() {
        return ruleHitRef;
    }

    public void setRuleHitRef(String ruleHitRef) {
        this.ruleHitRef = ruleHitRef;
    }

    public RiskAssessmentEntity getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(RiskAssessmentEntity riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public RuleVersionEntity getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(RuleVersionEntity ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public String getRuleCodeSnapshot() {
        return ruleCodeSnapshot;
    }

    public void setRuleCodeSnapshot(String ruleCodeSnapshot) {
        this.ruleCodeSnapshot = ruleCodeSnapshot;
    }

    public Integer getRuleVersionNumberSnapshot() {
        return ruleVersionNumberSnapshot;
    }

    public void setRuleVersionNumberSnapshot(Integer ruleVersionNumberSnapshot) {
        this.ruleVersionNumberSnapshot = ruleVersionNumberSnapshot;
    }

    public Integer getRiskPoints() {
        return riskPoints;
    }

    public void setRiskPoints(Integer riskPoints) {
        this.riskPoints = riskPoints;
    }

    public RuleSeverity getSeveritySnapshot() {
        return severitySnapshot;
    }

    public void setSeveritySnapshot(RuleSeverity severitySnapshot) {
        this.severitySnapshot = severitySnapshot;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public JsonNode getEvidence() {
        return evidence;
    }

    public void setEvidence(JsonNode evidence) {
        this.evidence = evidence;
    }

    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(Instant triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    
}
