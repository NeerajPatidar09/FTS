package com.lost.fts.device.infrastructure.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "devices", uniqueConstraints = {
        @UniqueConstraint(name = "ukdevicesfingerprinthash", columnNames = "fingerprinthash")
})
public class DeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store a keyed HMAC fingerprint, not the raw device fingerprint.
    @Column(name = "fingerprinthash", nullable = false, length = 128)
    private String fingerprintHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 20)
    private DevicePlatform platform;

    @Column(name = "devicemodel", length = 100)
    private String deviceModel;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "lastobservedat", nullable = false)
    private Instant lastObservedAt;

    public void recordObservation(Instant observedAt) {
        if (observedAt == null) {
            throw new IllegalArgumentException(
                    "Observation timestamp is required");
        }

        if (lastObservedAt == null || observedAt.isAfter(lastObservedAt)) {
            lastObservedAt = observedAt;
        }
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;

        if (lastObservedAt == null) {
            lastObservedAt = now;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFingerprintHash() {
        return fingerprintHash;
    }

    public void setFingerprintHash(String fingerprintHash) {
        this.fingerprintHash = fingerprintHash;
    }

    public DevicePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(DevicePlatform platform) {
        this.platform = platform;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastObservedAt() {
        return lastObservedAt;
    }

}
