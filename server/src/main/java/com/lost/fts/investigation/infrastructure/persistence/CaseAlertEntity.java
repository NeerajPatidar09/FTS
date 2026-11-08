package com.lost.fts.investigation.infrastructure.persistence;

import java.time.Instant;

import com.lost.fts.alert.infrastructure.persistence.AlertEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "casealerts", uniqueConstraints = {
        @UniqueConstraint(name = "ukcasealertspair", columnNames = { "caseid", "alertid" })
}, indexes = {
        @Index(name = "idxcasealertsalert", columnList = "alertid")
})
public class CaseAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caseid", nullable = false, foreignKey = @ForeignKey(name = "fkcasealertscase"))
    private CaseEntity investigationCase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alertid", nullable = false, foreignKey = @ForeignKey(name = "fkcasealertsalert"))
    private AlertEntity alert;

    @Column(name = "linkedby", nullable = false, length = 100)
    private String linkedBy;

    @Column(name = "linkreason", length = 1000)
    private String linkReason;

    @Column(name = "linkedat", nullable = false, updatable = false)
    private Instant linkedAt;

    protected CaseAlertEntity() {
        // Required by JPA
    }

    public CaseAlertEntity(
            CaseEntity investigationCase,
            AlertEntity alert,
            String linkedBy,
            String linkReason,
            Instant linkedAt) {

        if (investigationCase == null || alert == null) {
            throw new IllegalArgumentException(
                    "Case and alert are required");
        }

        if (linkedBy == null || linkedBy.isBlank() || linkedAt == null) {
            throw new IllegalArgumentException(
                    "Linking user and timestamp are required");
        }

        this.investigationCase = investigationCase;
        this.alert = alert;
        this.linkedBy = linkedBy.trim();
        this.linkReason = linkReason == null || linkReason.isBlank()
                ? null
                : linkReason.trim();
        this.linkedAt = linkedAt;
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

    public AlertEntity getAlert() {
        return alert;
    }

    public void setAlert(AlertEntity alert) {
        this.alert = alert;
    }

    public String getLinkedBy() {
        return linkedBy;
    }

    public void setLinkedBy(String linkedBy) {
        this.linkedBy = linkedBy;
    }

    public String getLinkReason() {
        return linkReason;
    }

    public void setLinkReason(String linkReason) {
        this.linkReason = linkReason;
    }

    public Instant getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(Instant linkedAt) {
        this.linkedAt = linkedAt;
    }

}
