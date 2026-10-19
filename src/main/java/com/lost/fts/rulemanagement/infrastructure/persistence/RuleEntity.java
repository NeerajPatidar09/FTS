package com.lost.fts.rulemanagement.infrastructure.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity 
@Table(name = "rules", uniqueConstraints = {
        @UniqueConstraint(name = "ukrulesrulecode", columnNames = "rulecode")
}, indexes = {
        @Index(name = "idxrulesdomainstatus", columnList = "domain, status")
})
public class RuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rulecode", nullable = false, length = 100)
    private String ruleCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RuleDomain domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RuleStatus status = RuleStatus.DISABLED;

    @Column(name = "createdby", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updatedat", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "rowversion", nullable = false)
    private Long rowVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RuleDomain getDomain() {
        return domain;
    }

    public void setDomain(RuleDomain domain) {
        this.domain = domain;
    }

    public RuleStatus getStatus() {
        return status;
    }

    public void setStatus(RuleStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
