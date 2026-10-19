package com.lost.fts.investigation.infrastructure.persistence;

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
import jakarta.persistence.Table;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "caseactions", indexes = {
        @Index(name = "idxcaseactionscasetime", columnList = "caseid, performedat"),
        @Index(name = "idxcaseactionsactor", columnList = "actorid, performedat")
})
public class CaseActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caseid", nullable = false, foreignKey = @ForeignKey(name = "fkcaseactionscase"))
    private CaseEntity investigationCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "actiontype", nullable = false, length = 40)
    private CaseActionType actionType;

    @Column(name = "actorid", nullable = false, length = 100)
    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previousstatus", length = 30)
    private CaseStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "newstatus", length = 30)
    private CaseStatus newStatus;

    @Column(nullable = false, length = 1500)
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "actiondetails", columnDefinition = "json")
    private JsonNode actionDetails;

    @Column(name = "performedat", nullable = false, updatable = false)
    private Instant performedAt;

    protected CaseActionEntity() {
        // Required by JPA
    }

    public CaseActionEntity(
            CaseEntity investigationCase,
            CaseActionType actionType,
            String actorId,
            CaseStatus previousStatus,
            CaseStatus newStatus,
            String summary,
            JsonNode actionDetails,
            Instant performedAt) {

        if (investigationCase == null || actionType == null) {
            throw new IllegalArgumentException(
                    "Case and action type are required");
        }

        if (actorId == null || actorId.isBlank()
                || summary == null || summary.isBlank()
                || performedAt == null) {
            throw new IllegalArgumentException(
                    "Actor, summary and action timestamp are required");
        }

        this.investigationCase = investigationCase;
        this.actionType = actionType;
        this.actorId = actorId.trim();
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.summary = summary.trim();
        this.actionDetails = actionDetails == null
                ? null
                : actionDetails.deepCopy();
        this.performedAt = performedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CaseEntity getInvestigationCase() {
        return investigationCase;
    }

    public void setInvestigationCase(CaseEntity investigationCase) {
        this.investigationCase = investigationCase;
    }

    public CaseActionType getActionType() {
        return actionType;
    }

    public void setActionType(CaseActionType actionType) {
        this.actionType = actionType;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public CaseStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(CaseStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public CaseStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(CaseStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public JsonNode getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(JsonNode actionDetails) {
        this.actionDetails = actionDetails;
    }

    public Instant getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(Instant performedAt) {
        this.performedAt = performedAt;
    }

}
