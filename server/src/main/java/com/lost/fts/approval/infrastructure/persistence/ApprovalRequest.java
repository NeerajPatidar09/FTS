package com.lost.fts.approval.infrastructure.persistence;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_reference", nullable = false, unique = true, length = 50)
    private String requestReference;

    @Column(name = "module_code", nullable = false, length = 50)
    private String moduleCode;

    @Column(name = "action_code", nullable = false, length = 50)
    private String actionCode;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "maker_user_id", nullable = false)
    private Long makerUserId;

    @Column(name = "checker_user_id")
    private Long checkerUserId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload", nullable = false, columnDefinition = "json")
    private String requestPayload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(name = "maker_comments", length = 1000)
    private String makerComments;

    @Column(name = "checker_comments", length = 1000)
    private String checkerComments;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    void onCreate() {
        if (submittedAt == null) {
            submittedAt = Instant.now();
        }
    }

    public void approve(Long checkerId, String comments) {
        this.status = ApprovalStatus.APPROVED;
        this.checkerUserId = checkerId;
        this.checkerComments = comments;
        this.decidedAt = Instant.now();
    }

    public void reject(Long checkerId, String comments) {
        this.status = ApprovalStatus.REJECTED;
        this.checkerUserId = checkerId;
        this.checkerComments = comments;
        this.decidedAt = Instant.now();
    }

    public void cancel() {
        this.status = ApprovalStatus.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestReference() {
        return requestReference;
    }

    public void setRequestReference(String requestReference) {
        this.requestReference = requestReference;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getMakerUserId() {
        return makerUserId;
    }

    public void setMakerUserId(Long makerUserId) {
        this.makerUserId = makerUserId;
    }

    public Long getCheckerUserId() {
        return checkerUserId;
    }

    public void setCheckerUserId(Long checkerUserId) {
        this.checkerUserId = checkerUserId;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getMakerComments() {
        return makerComments;
    }

    public void setMakerComments(String makerComments) {
        this.makerComments = makerComments;
    }

    public String getCheckerComments() {
        return checkerComments;
    }

    public void setCheckerComments(String checkerComments) {
        this.checkerComments = checkerComments;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}