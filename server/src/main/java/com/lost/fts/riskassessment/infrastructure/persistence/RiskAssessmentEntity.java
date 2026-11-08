package com.lost.fts.riskassessment.infrastructure.persistence;

import java.time.Instant;

import com.lost.fts.transaction.infrastructure.persistence.TransactionEntity;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "riskassessments", uniqueConstraints = {
        @UniqueConstraint(name = "ukriskassessmentsref", columnNames = "assessmentref")
}, indexes = {
        @Index(name = "idxassessmentstransactioncreated", columnList = "transactionid, createdat"),
        @Index(name = "idxassessmentsstatuscreated", columnList = "status, createdat")
})
public class RiskAssessmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assessmentref", nullable = false, length = 64)
    private String assessmentRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transactionid", nullable = false, foreignKey = @ForeignKey(name = "fkassessmentstransaction"))
    private TransactionEntity transaction;

    // Reference to the event that caused this assessment, if available.
    @Column(name = "sourceeventref", length = 128)
    private String sourceEventRef;

    @Column(name = "engineversion", nullable = false, length = 50)
    private String engineVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssessmentStatus status = AssessmentStatus.PROCESSING;

    // Null until the assessment finishes.
    @Column(name = "riskscore")
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risklevel", length = 20)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", length = 30)
    private AssessmentOutcome outcome;

    @Column(name = "summary", length = 1000)
    private String summary;

    @Column(name = "failurecode", length = 100)
    private String failureCode;

    @Column(name = "startedat", nullable = false)
    private Instant startedAt;

    @Column(name = "evaluatedat")
    private Instant evaluatedAt;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updatedat", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "rowversion", nullable = false)
    private Long rowVersion;

    protected RiskAssessmentEntity() {
        // Required by JPA
    }

    public RiskAssessmentEntity(
            String assessmentRef,
            TransactionEntity transaction,
            String sourceEventRef,
            String engineVersion,
            Instant startedAt) {

        if (assessmentRef == null || assessmentRef.isBlank()) {
            throw new IllegalArgumentException(
                    "Assessment reference is required");
        }

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction is required");
        }

        if (engineVersion == null || engineVersion.isBlank()) {
            throw new IllegalArgumentException(
                    "Engine version is required");
        }

        if (startedAt == null) {
            throw new IllegalArgumentException(
                    "Assessment start time is required");
        }

        this.assessmentRef = assessmentRef.trim();
        this.transaction = transaction;
        this.sourceEventRef = normalizeOptional(sourceEventRef);
        this.engineVersion = engineVersion.trim();
        this.startedAt = startedAt;
        this.status = AssessmentStatus.PROCESSING;
    }

    public void complete(
            int riskScore,
            RiskLevel riskLevel,
            AssessmentOutcome outcome,
            String summary,
            Instant evaluatedAt) {

        if (status != AssessmentStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing assessments can be completed");
        }

        if (riskScore < 0 || riskScore > 100) {
            throw new IllegalArgumentException(
                    "Risk score must be between 0 and 100");
        }

        if (riskLevel == null || outcome == null || evaluatedAt == null) {
            throw new IllegalArgumentException(
                    "Risk level, outcome and evaluation time are required");
        }

        if (evaluatedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException(
                    "Evaluation time cannot precede the start time");
        }

        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.outcome = outcome;
        this.summary = normalizeOptional(summary);
        this.evaluatedAt = evaluatedAt;
        this.status = AssessmentStatus.COMPLETED;
        this.failureCode = null;
    }

    public void markFailed(String failureCode, Instant evaluatedAt) {
        if (status != AssessmentStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing assessments can fail");
        }

        if (failureCode == null || failureCode.isBlank()
                || evaluatedAt == null) {
            throw new IllegalArgumentException(
                    "Failure code and evaluation time are required");
        }

        if (evaluatedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException(
                    "Evaluation time cannot precede the start time");
        }

        this.failureCode = failureCode.trim();
        this.evaluatedAt = evaluatedAt;
        this.status = AssessmentStatus.FAILED;
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssessmentRef() {
        return assessmentRef;
    }

    public void setAssessmentRef(String assessmentRef) {
        this.assessmentRef = assessmentRef;
    }

    public TransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionEntity transaction) {
        this.transaction = transaction;
    }

    public String getSourceEventRef() {
        return sourceEventRef;
    }

    public void setSourceEventRef(String sourceEventRef) {
        this.sourceEventRef = sourceEventRef;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public AssessmentOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(AssessmentOutcome outcome) {
        this.outcome = outcome;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(Instant evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

}
