package com.lost.fts.investigation.infrastructure.persistence;

import com.lost.fts.customer.infrastructure.persistence.CustomerEntity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "investigations", indexes = {
        @Index(name = "idx_investigation_reference", columnList = "investigation_ref", unique = true),
        @Index(name = "idx_investigation_status", columnList = "status"),
        @Index(name = "idx_investigation_customer", columnList = "customer_id")
})
public class Investigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "investigation_ref", nullable = false, unique = true, length = 50)
    private String investigationRef;

    @Column(name = "investigation_title", nullable = false, length = 200)
    private String investigationTitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InvestigationStatus status = InvestigationStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvestigationPriority priority = InvestigationPriority.MEDIUM;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (openedAt == null) {
            openedAt = now;
        }

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getInvestigationRef() {
        return investigationRef;
    }

    public void setInvestigationRef(String investigationRef) {
        this.investigationRef = investigationRef;
    }

    public String getInvestigationTitle() {
        return investigationTitle;
    }

    public void setInvestigationTitle(String investigationTitle) {
        this.investigationTitle = investigationTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public InvestigationStatus getStatus() {
        return status;
    }

    public void setStatus(InvestigationStatus status) {
        this.status = status;
    }

    public InvestigationPriority getPriority() {
        return priority;
    }

    public void setPriority(InvestigationPriority priority) {
        this.priority = priority;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}