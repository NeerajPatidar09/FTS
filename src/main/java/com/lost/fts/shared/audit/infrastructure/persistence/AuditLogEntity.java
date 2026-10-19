package com.lost.fts.shared.audit.infrastructure.persistence;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "auditlogs", uniqueConstraints = {
        @UniqueConstraint(name = "ukauditlogsref", columnNames = "auditref")
}, indexes = {
        @Index(name = "idxauditlogsactortime", columnList = "actorid, occurredat"),
        @Index(name = "idxauditlogsresourcetime", columnList = "resourcetype, resourceref, occurredat"),
        @Index(name = "idxauditlogscorrelation", columnList = "correlationid")
})
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auditref", nullable = false, length = 64)
    private String auditRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "actortype", nullable = false, length = 30)
    private AuditActorType actorType;

    // Use an internal user ID or stable system/service identity.
    @Column(name = "actorid", nullable = false, length = 100)
    private String actorId;

    @Column(name = "actioncode", nullable = false, length = 100)
    private String actionCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditOutcome outcome;

    @Column(name = "resourcetype", nullable = false, length = 100)
    private String resourceType;

    @Column(name = "resourceref", length = 128)
    private String resourceRef;

    @Column(name = "correlationid", length = 128)
    private String correlationId;

    // Store minimal, sanitized audit details only.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "json")
    private JsonNode details;

    // When the audited action occurred.
    @Column(name = "occurredat", nullable = false, updatable = false)
    private Instant occurredAt;

    // When this audit record was persisted.
    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    protected AuditLogEntity() {
        // Required by JPA
    }

    public AuditLogEntity(
            String auditRef,
            AuditActorType actorType,
            String actorId,
            String actionCode,
            AuditOutcome outcome,
            String resourceType,
            String resourceRef,
            String correlationId,
            JsonNode details,
            Instant occurredAt) {

        if (isBlank(auditRef)
                || isBlank(actorId)
                || isBlank(actionCode)
                || isBlank(resourceType)) {
            throw new IllegalArgumentException(
                    "Audit reference, actor, action and resource type are required");
        }

        if (actorType == null || outcome == null || occurredAt == null) {
            throw new IllegalArgumentException(
                    "Actor type, outcome and occurrence time are required");
        }

        this.auditRef = auditRef.trim();
        this.actorType = actorType;
        this.actorId = actorId.trim();
        this.actionCode = actionCode.trim();
        this.outcome = outcome;
        this.resourceType = resourceType.trim();
        this.resourceRef = normalizeOptional(resourceRef);
        this.correlationId = normalizeOptional(correlationId);
        this.details = details == null ? null : details.deepCopy();
        this.occurredAt = occurredAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuditRef() {
        return auditRef;
    }

    public void setAuditRef(String auditRef) {
        this.auditRef = auditRef;
    }

    public AuditActorType getActorType() {
        return actorType;
    }

    public void setActorType(AuditActorType actorType) {
        this.actorType = actorType;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public AuditOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(AuditOutcome outcome) {
        this.outcome = outcome;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceRef() {
        return resourceRef;
    }

    public void setResourceRef(String resourceRef) {
        this.resourceRef = resourceRef;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public JsonNode getDetails() {
        return details;
    }

    public void setDetails(JsonNode details) {
        this.details = details;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
