package com.lost.fts.shared.audit.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findByActorIdOrderByOccurredAtDesc(String actorId);
    List<AuditLogEntity> findByResourceTypeAndResourceRefOrderByOccurredAtDesc(String resourceType, String resourceRef);
    List<AuditLogEntity> findByCorrelationId(String correlationId);
}
