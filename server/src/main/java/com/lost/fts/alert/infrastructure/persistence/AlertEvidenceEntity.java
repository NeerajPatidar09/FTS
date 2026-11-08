package com.lost.fts.alert.infrastructure.persistence;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.lost.fts.detection.infrastructure.persistence.RuleHitEntity;

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
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "alertevidence", indexes = {
        @Index(name = "idxalertevidencealert", columnList = "alertid"),
        @Index(name = "idxalertevidencerulehit", columnList = "rulehitid")
})
public class AlertEvidenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alertid", nullable = false, foreignKey = @ForeignKey(name = "fkevidencealert"))
    private AlertEntity alert;

    // Optional because not every evidence item originates from a rule hit.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rulehitid", foreignKey = @ForeignKey(name = "fkevidencerulehit"))
    private RuleHitEntity ruleHit;

    @Enumerated(EnumType.STRING)
    @Column(name = "evidencetype", nullable = false, length = 40)
    private EvidenceType evidenceType;

    // Reference to the source record, not a copy of sensitive identifiers.
    @Column(name = "evidenceref", nullable = false, length = 160)
    private String evidenceRef;

    @Column(nullable = false, length = 1500)
    private String summary;

    // Compact evidence snapshot supporting an investigation.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evidencedata", columnDefinition = "json")
    private JsonNode evidenceData;

    @Column(name = "capturedat", nullable = false)
    private Instant capturedAt;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    protected AlertEvidenceEntity() {
        // Required by JPA
    }

    public AlertEvidenceEntity(
            AlertEntity alert,
            RuleHitEntity ruleHit,
            EvidenceType evidenceType,
            String evidenceRef,
            String summary,
            JsonNode evidenceData,
            Instant capturedAt) {

        if (alert == null) {
            throw new IllegalArgumentException("Alert is required");
        }

        if (evidenceType == null) {
            throw new IllegalArgumentException(
                    "Evidence type is required");
        }

        if (evidenceRef == null || evidenceRef.isBlank()
                || summary == null || summary.isBlank()
                || capturedAt == null) {
            throw new IllegalArgumentException(
                    "Evidence reference, summary and capture time are required");
        }

        if (ruleHit != null
                && alert.getRiskAssessment().getId() != null
                && ruleHit.getRiskAssessment().getId() != null
                && !alert.getRiskAssessment().getId()
                        .equals(ruleHit.getRiskAssessment().getId())) {
            throw new IllegalArgumentException(
                    "Rule hit and alert must belong to the same assessment");
        }

        this.alert = alert;
        this.ruleHit = ruleHit;
        this.evidenceType = evidenceType;
        this.evidenceRef = evidenceRef.trim();
        this.summary = summary.trim();
        this.evidenceData = evidenceData == null
                ? null
                : evidenceData.deepCopy();
        this.capturedAt = capturedAt;
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

    public AlertEntity getAlert() {
        return alert;
    }

    public void setAlert(AlertEntity alert) {
        this.alert = alert;
    }

    public RuleHitEntity getRuleHit() {
        return ruleHit;
    }

    public void setRuleHit(RuleHitEntity ruleHit) {
        this.ruleHit = ruleHit;
    }

    public EvidenceType getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(EvidenceType evidenceType) {
        this.evidenceType = evidenceType;
    }

    public String getEvidenceRef() {
        return evidenceRef;
    }

    public void setEvidenceRef(String evidenceRef) {
        this.evidenceRef = evidenceRef;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public JsonNode getEvidenceData() {
        return evidenceData;
    }

    public void setEvidenceData(JsonNode evidenceData) {
        this.evidenceData = evidenceData;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
