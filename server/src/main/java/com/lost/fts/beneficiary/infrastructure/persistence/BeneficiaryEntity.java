package com.lost.fts.beneficiary.infrastructure.persistence;

import java.time.Instant;

import com.lost.fts.account.infrastructure.persistence.AccountEntity;

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

@Entity
@Table(name = "beneficiaries", uniqueConstraints = {
        @UniqueConstraint(name = "ukbeneficiariesref", columnNames = "beneficiaryref")
}, indexes = {
        @Index(name = "idxbeneficiariesaccountstatus", columnList = "accountid, status")
})
public class BeneficiaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beneficiaryref", nullable = false, length = 64)
    private String beneficiaryRef;

    // The account that owns this saved beneficiary.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountid", nullable = false, foreignKey = @ForeignKey(name = "fkbeneficiariesaccount"))
    private AccountEntity account;

    @Column(name = "beneficiaryname", nullable = false, length = 150)
    private String beneficiaryName;

    @Enumerated(EnumType.STRING)
    @Column(name = "beneficiarytype", nullable = false, length = 30)
    private BeneficiaryType beneficiaryType;

    // Opaque reference to a securely stored destination identifier.
    // Do not store raw bank account numbers or UPI IDs in plaintext here.
    @Column(name = "destinationreferencetoken", nullable = false, length = 128)
    private String destinationReferenceToken;

    @Column(name = "destinationmasked", length = 50)
    private String destinationMasked;

    @Column(name = "bankifsc", length = 11)
    private String bankIfsc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BeneficiaryStatus status = BeneficiaryStatus.PENDING;

    @Column(name = "activatedat")
    private Instant activatedAt;

    @Column(name = "lastusedat")
    private Instant lastUsedAt;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updatedat", nullable = false)
    private Instant updatedAt;

    public void activate() {
        if (status != BeneficiaryStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending beneficiary can be activated");
        }
        status = BeneficiaryStatus.ACTIVE;
        activatedAt = Instant.now();
    }

    public void suspend() {
        if (status == BeneficiaryStatus.ACTIVE) {
            status = BeneficiaryStatus.SUSPENDED;
        }
    }

    public void recordUsage(Instant usedAt) {
        if (status != BeneficiaryStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only an active beneficiary can be used");
        }

        lastUsedAt = usedAt;
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

    public String getBeneficiaryRef() {
        return beneficiaryRef;
    }

    public void setBeneficiaryRef(String beneficiaryRef) {
        this.beneficiaryRef = beneficiaryRef;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public BeneficiaryType getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(BeneficiaryType beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    public String getDestinationReferenceToken() {
        return destinationReferenceToken;
    }

    public void setDestinationReferenceToken(String destinationReferenceToken) {
        this.destinationReferenceToken = destinationReferenceToken;
    }

    public String getDestinationMasked() {
        return destinationMasked;
    }

    public void setDestinationMasked(String destinationMasked) {
        this.destinationMasked = destinationMasked;
    }

    public String getBankIfsc() {
        return bankIfsc;
    }

    public void setBankIfsc(String bankIfsc) {
        this.bankIfsc = bankIfsc;
    }

    public BeneficiaryStatus getStatus() {
        return status;
    }

    public void setStatus(BeneficiaryStatus status) {
        this.status = status;
    }

    public Instant getActivatedAt() {
        return activatedAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}