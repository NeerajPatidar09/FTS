package com.lost.fts.messaging.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, Long> {

    boolean existsByConsumerNameAndEventId(String consumerName, String eventId);
}
