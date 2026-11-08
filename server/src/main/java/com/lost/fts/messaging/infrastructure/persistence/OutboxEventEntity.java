package com.lost.fts.messaging.infrastructure.persistence;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "outboxevents", uniqueConstraints = {
        @UniqueConstraint(name = "ukoutboxeventseventid", columnNames = "eventid")
}, indexes = {
        @Index(name = "idxoutboxeventsretry", columnList = "status, nextattemptat, createdat"),
        @Index(name = "idxoutboxeventslease", columnList = "status, leaseuntil"),
        @Index(name = "idxoutboxeventsaggregate", columnList = "aggregatetype, aggregateid")
})
public class OutboxEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stable unique event ID included in the Kafka message.
    @Column(name = "eventid", nullable = false, length = 160)
    private String eventId;

    @Column(name = "destinationtopic", nullable = false, length = 249)
    private String destinationTopic;

    @Column(name = "aggregatetype", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregateid", nullable = false, length = 160)
    private String aggregateId;

    @Column(name = "eventtype", nullable = false, length = 150)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    private JsonNode payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(name = "nextattemptat")
    private Instant nextAttemptAt;

    // Lease metadata supports competing publisher workers.
    @Column(name = "claimedby", length = 100)
    private String claimedBy;

    @Column(name = "leaseuntil")
    private Instant leaseUntil;

    @Column(name = "publishedat")
    private Instant publishedAt;

    // Store a controlled error code, not raw exception details or payloads.
    @Column(name = "lasterrorcode", length = 100)
    private String lastErrorCode;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(name = "rowversion", nullable = false)
    private Long rowVersion;

    protected OutboxEventEntity() {
        // Required by JPA
    }

    public OutboxEventEntity(
            String eventId,
            String destinationTopic,
            String aggregateType,
            String aggregateId,
            String eventType,
            JsonNode payload,
            Instant createdAt) {

        if (isBlank(eventId)
                || isBlank(destinationTopic)
                || isBlank(aggregateType)
                || isBlank(aggregateId)
                || isBlank(eventType)) {
            throw new IllegalArgumentException(
                    "Event ID, topic, aggregate and event type are required");
        }

        if (payload == null || !payload.isObject()) {
            throw new IllegalArgumentException(
                    "Event payload must be a JSON object");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException(
                    "Creation timestamp is required");
        }

        this.eventId = eventId.trim();
        this.destinationTopic = destinationTopic.trim();
        this.aggregateType = aggregateType.trim();
        this.aggregateId = aggregateId.trim();
        this.eventType = eventType.trim();
        this.payload = payload.deepCopy();

        this.status = OutboxStatus.PENDING;
        this.attempts = 0;
        this.createdAt = createdAt;
        this.nextAttemptAt = createdAt;
    }

    /**
     * Claims an event for publication.
     * The caller must lock the selected database row while claiming it.
     */
    public void claim(
            String workerId,
            Instant now,
            Instant requestedLeaseUntil) {

        if (isBlank(workerId) || now == null || requestedLeaseUntil == null) {
            throw new IllegalArgumentException(
                    "Worker ID and lease timestamps are required");
        }

        if (!requestedLeaseUntil.isAfter(now)) {
            throw new IllegalArgumentException(
                    "Lease must expire after the claim time");
        }

        if (status == OutboxStatus.PUBLISHED
                || status == OutboxStatus.FAILED) {
            throw new IllegalStateException(
                    "Terminal outbox events cannot be claimed");
        }

        if (status == OutboxStatus.INPROGRESS
                && leaseUntil != null
                && leaseUntil.isAfter(now)) {
            throw new IllegalStateException(
                    "The event is already leased to another worker");
        }

        if (nextAttemptAt != null && nextAttemptAt.isAfter(now)) {
            throw new IllegalStateException(
                    "The event is not due for another attempt");
        }

        this.status = OutboxStatus.INPROGRESS;
        this.claimedBy = workerId.trim();
        this.leaseUntil = requestedLeaseUntil;
        this.attempts++;
    }

    public void markPublished(Instant publishedAt) {
        if (status != OutboxStatus.INPROGRESS) {
            throw new IllegalStateException(
                    "Only an in-progress event can be marked published");
        }

        if (publishedAt == null) {
            throw new IllegalArgumentException(
                    "Publication timestamp is required");
        }

        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = publishedAt;
        this.lastErrorCode = null;
        clearLease();
        this.nextAttemptAt = null;
    }

    /**
     * Schedules a retry or permanently fails the event once the
     * configured maximum number of attempts has been reached.
     */
    public void recordPublishFailure(
            String errorCode,
            Instant retryAt,
            int maxAttempts) {

        if (status != OutboxStatus.INPROGRESS) {
            throw new IllegalStateException(
                    "Only an in-progress event can record a publish failure");
        }

        if (isBlank(errorCode) || errorCode.length() > 100) {
            throw new IllegalArgumentException(
                    "A short, controlled error code is required");
        }

        if (maxAttempts < 1) {
            throw new IllegalArgumentException(
                    "Maximum attempts must be positive");
        }

        this.lastErrorCode = errorCode.trim();
        clearLease();

        if (attempts >= maxAttempts) {
            this.status = OutboxStatus.FAILED;
            this.nextAttemptAt = null;
        } else {
            if (retryAt == null) {
                throw new IllegalArgumentException(
                        "Retry time is required while attempts remain");
            }

            this.status = OutboxStatus.PENDING;
            this.nextAttemptAt = retryAt;
        }
    }

    private void clearLease() {
        this.claimedBy = null;
        this.leaseUntil = null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDestinationTopic() {
        return destinationTopic;
    }

    public void setDestinationTopic(String destinationTopic) {
        this.destinationTopic = destinationTopic;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void setNextAttemptAt(Instant nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    public Instant getLeaseUntil() {
        return leaseUntil;
    }

    public void setLeaseUntil(Instant leaseUntil) {
        this.leaseUntil = leaseUntil;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getLastErrorCode() {
        return lastErrorCode;
    }

    public void setLastErrorCode(String lastErrorCode) {
        this.lastErrorCode = lastErrorCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(Long rowVersion) {
        this.rowVersion = rowVersion;
    }

}
