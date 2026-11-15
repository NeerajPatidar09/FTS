package com.lost.fts.security.mfa.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_mfa_recovery_codes", indexes = {
        @Index(name = "idx_recovery_mfa", columnList = "user_mfa_id")
})
public class MfaRecoveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_mfa_id", nullable = false)
    private UserMfa userMfa;

    @Column(name = "code_hash", nullable = false, length = 64)
    private String codeHash;

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void markUsed() {
        this.used = true;
    }

    public Long getId() {
        return id;
    }

    public UserMfa getUserMfa() {
        return userMfa;
    }

    public void setUserMfa(UserMfa userMfa) {
        this.userMfa = userMfa;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public void setCodeHash(String codeHash) {
        this.codeHash = codeHash;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}