package com.lost.fts.messaging.infrastructure.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "processedevents", uniqueConstraints = {
        @UniqueConstraint(name = "ukprocessedeventsconsumerevent", columnNames = { "consumername", "eventid" })
}, indexes = {
        @Index(name = "idxprocessedeventstypetime", columnList = "eventtype, processedat")
})
public class ProcessedEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stable logical identity of the consumer, not an individual pod ID.
    @Column(name = "consumername", nullable = false, length = 100)
    private String consumerName;

    // Stable event ID supplied by the event producer.
    @Column(name = "eventid", nullable = false, length = 160)
    private String eventId;

    @Column(name = "eventtype", nullable = false, length = 150)
    private String eventType;

    // Optional digest for detecting an event ID reused with different content.
    @Column(name = "payloadhash", length = 128)
    private String payloadHash;

    @Column(name = "processedat", nullable = false, updatable = false)
    private Instant processedAt;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProcessedEventEntity() {
        // Required by JPA
    }

    public ProcessedEventEntity(
            String consumerName,
            String eventId,
            String eventType,
            String payloadHash,
            Instant processedAt) {

        if (isBlank(consumerName)
                || isBlank(eventId)
                || isBlank(eventType)) {
            throw new IllegalArgumentException(
                    "Consumer name, event ID and event type are required");
        }

        if (processedAt == null) {
            throw new IllegalArgumentException(
                    "Processing timestamp is required");
        }

        this.consumerName = consumerName.trim();
        this.eventId = eventId.trim();
        this.eventType = eventType.trim();
        this.payloadHash = normalizeOptional(payloadHash);
        this.processedAt = processedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String normalizeOptional(String value) {
        return isBlank(value) ? null : value.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayloadHash() {
        return payloadHash;
    }

    public void setPayloadHash(String payloadHash) {
        this.payloadHash = payloadHash;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
