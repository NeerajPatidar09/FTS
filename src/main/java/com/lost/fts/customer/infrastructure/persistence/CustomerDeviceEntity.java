package com.lost.fts.customer.infrastructure.persistence;

import java.time.Instant;

import com.lost.fts.device.infrastructure.persistence.DeviceEntity;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "customerdevices", uniqueConstraints = {
        @UniqueConstraint(name = "ukcustomerdevicescustomerdevice", columnNames = { "customerid", "deviceid" })
}, indexes = {
        @Index(name = "idxcustomerdevicesdeviceid", columnList = "deviceid")
})
public class CustomerDeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customerid", nullable = false, foreignKey = @ForeignKey(name = "fkcustomerdevicescustomer"))
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deviceid", nullable = false, foreignKey = @ForeignKey(name = "fkcustomerdevicesdevice"))
    private DeviceEntity device;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerDeviceStatus status = CustomerDeviceStatus.REGISTERED;

    @Column(name = "firstseenat", nullable = false)
    private Instant firstSeenAt;

    @Column(name = "lastseenat", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "lastsuccessfulauthat")
    private Instant lastSuccessfulAuthAt;

    @Column(name = "failedauthcount", nullable = false)
    private int failedAuthCount;

    public void recordSuccessfulAuthentication(Instant occurredAt) {
        validateTimestamp(occurredAt);

        updateLastSeen(occurredAt);
        lastSuccessfulAuthAt = occurredAt;
        failedAuthCount = 0;
    }

    public void recordFailedAuthentication(Instant occurredAt) {
        validateTimestamp(occurredAt);

        updateLastSeen(occurredAt);
        failedAuthCount++;
    }

    public void trust() {
        if (status == CustomerDeviceStatus.BLOCKED
                || status == CustomerDeviceStatus.REVOKED) {
            throw new IllegalStateException(
                    "Blocked or revoked devices cannot be trusted directly");
        }

        status = CustomerDeviceStatus.TRUSTED;
    }

    public void block() {
        status = CustomerDeviceStatus.BLOCKED;
    }

    public void revoke() {
        status = CustomerDeviceStatus.REVOKED;
    }

    private void updateLastSeen(Instant occurredAt) {
        if (occurredAt.isAfter(lastSeenAt)) {
            lastSeenAt = occurredAt;
        }
    }

    private void validateTimestamp(Instant timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException(
                    "Event timestamp is required");
        }
    }

    @PrePersist
    protected void onCreate() {
        if (firstSeenAt == null || lastSeenAt == null) {
            throw new IllegalStateException(
                    "First-seen and last-seen timestamps are required");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public DeviceEntity getDevice() {
        return device;
    }

    public void setDevice(DeviceEntity device) {
        this.device = device;
    }

    public CustomerDeviceStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerDeviceStatus status) {
        this.status = status;
    }

    public Instant getFirstSeenAt() {
        return firstSeenAt;
    }

    public void setFirstSeenAt(Instant firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Instant getLastSuccessfulAuthAt() {
        return lastSuccessfulAuthAt;
    }

    public void setLastSuccessfulAuthAt(Instant lastSuccessfulAuthAt) {
        this.lastSuccessfulAuthAt = lastSuccessfulAuthAt;
    }

    public int getFailedAuthCount() {
        return failedAuthCount;
    }

    public void setFailedAuthCount(int failedAuthCount) {
        this.failedAuthCount = failedAuthCount;
    }

}
