package com.lost.fts.investigation.infrastructure.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "investigationcases", uniqueConstraints = {
        @UniqueConstraint(name = "ukinvestigationcasesref", columnNames = "caseref")
}, indexes = {
        @Index(name = "idxcasesstatuspriority", columnList = "status, priority, createdat"),
        @Index(name = "idxcasesassigneestatus", columnList = "assignedto, status"),
        @Index(name = "idxcasesdomain", columnList = "domain, createdat")
})
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "caseref", nullable = false, length = 64)
    private String caseRef;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CaseDomain domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CasePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CaseStatus status = CaseStatus.NEW;

    @Column(name = "assignedto", length = 100)
    private String assignedTo;

    @Column(name = "createdby", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "openedat", nullable = false)
    private Instant openedAt;

    @Column(name = "sladueat")
    private Instant slaDueAt;

    @Column(name = "resolvedat")
    private Instant resolvedAt;

    @Column(name = "closedat")
    private Instant closedAt;

    @Column(name = "resolutioncode", length = 100)
    private String resolutionCode;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updatedat", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "rowversion", nullable = false)
    private Long rowVersion;

    protected CaseEntity() {
        // Required by JPA
    }

    public CaseEntity(
            String caseRef,
            String title,
            String description,
            CaseDomain domain,
            CasePriority priority,
            String createdBy,
            Instant openedAt,
            Instant slaDueAt) {

        if (isBlank(caseRef) || isBlank(title) || isBlank(createdBy)) {
            throw new IllegalArgumentException(
                    "Case reference, title and creator are required");
        }

        if (domain == null || priority == null || openedAt == null) {
            throw new IllegalArgumentException(
                    "Domain, priority and opening time are required");
        }

        if (slaDueAt != null && slaDueAt.isBefore(openedAt)) {
            throw new IllegalArgumentException(
                    "SLA deadline cannot be before case opening time");
        }

        this.caseRef = caseRef.trim();
        this.title = title.trim();
        this.description = normalizeOptional(description);
        this.domain = domain;
        this.priority = priority;
        this.createdBy = createdBy.trim();
        this.openedAt = openedAt;
        this.slaDueAt = slaDueAt;
        this.status = CaseStatus.NEW;
    }

    public void assignTo(String username) {
        ensureOpen();

        if (isBlank(username)) {
            throw new IllegalArgumentException("Assignee is required");
        }

        this.assignedTo = username.trim();

        if (status == CaseStatus.NEW) {
            status = CaseStatus.ASSIGNED;
        }
    }

    public void startInvestigation() {
        if (status != CaseStatus.NEW && status != CaseStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Case cannot start investigation from status " + status);
        }

        if (isBlank(assignedTo)) {
            throw new IllegalStateException(
                    "Case must be assigned before investigation starts");
        }

        status = CaseStatus.INPROGRESS;
    }

    public void markPendingReview() {
        if (status != CaseStatus.INPROGRESS) {
            throw new IllegalStateException(
                    "Only an in-progress case can enter review");
        }

        status = CaseStatus.PENDINGREVIEW;
    }

    public void escalate() {
        if (status != CaseStatus.INPROGRESS
                && status != CaseStatus.PENDINGREVIEW
                && status != CaseStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Case cannot be escalated from status " + status);
        }

        status = CaseStatus.ESCALATED;
    }

    public void resolve(String resolutionCode, Instant resolvedAt) {
        if (status != CaseStatus.INPROGRESS
                && status != CaseStatus.PENDINGREVIEW
                && status != CaseStatus.ESCALATED) {
            throw new IllegalStateException(
                    "Case must be under investigation or escalated to resolve");
        }

        if (isBlank(resolutionCode) || resolvedAt == null) {
            throw new IllegalArgumentException(
                    "Resolution code and timestamp are required");
        }

        this.resolutionCode = resolutionCode.trim();
        this.resolvedAt = resolvedAt;
        this.status = CaseStatus.RESOLVED;
    }

    public void close(Instant closedAt) {
        if (status != CaseStatus.RESOLVED) {
            throw new IllegalStateException(
                    "Only a resolved case can be closed");
        }

        if (closedAt == null
                || (resolvedAt != null && closedAt.isBefore(resolvedAt))) {
            throw new IllegalArgumentException(
                    "Closing timestamp must be after resolution");
        }

        this.closedAt = closedAt;
        this.status = CaseStatus.CLOSED;
    }

    public void changePriority(CasePriority priority) {
        ensureOpen();

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Priority is required");
        }

        this.priority = priority;
    }

    private void ensureOpen() {
        if (status == CaseStatus.RESOLVED || status == CaseStatus.CLOSED) {
            throw new IllegalStateException(
                    "Resolved or closed cases cannot be modified this way");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
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

    public String getCaseRef() {
        return caseRef;
    }

    public void setCaseRef(String caseRef) {
        this.caseRef = caseRef;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CaseDomain getDomain() {
        return domain;
    }

    public void setDomain(CaseDomain domain) {
        this.domain = domain;
    }

    public CasePriority getPriority() {
        return priority;
    }

    public void setPriority(CasePriority priority) {
        this.priority = priority;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getSlaDueAt() {
        return slaDueAt;
    }

    public void setSlaDueAt(Instant slaDueAt) {
        this.slaDueAt = slaDueAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public String getResolutionCode() {
        return resolutionCode;
    }

    public void setResolutionCode(String resolutionCode) {
        this.resolutionCode = resolutionCode;
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
