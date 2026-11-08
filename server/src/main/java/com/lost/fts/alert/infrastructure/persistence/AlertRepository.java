package com.lost.fts.alert.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {

    Optional<AlertEntity> findByAlertRef(String alertRef);
    boolean existsByDeduplicationKey(String deduplicationKey);
    Optional<AlertEntity> findByDeduplicationKey(String deduplicationKey);

    List<AlertEntity> findByStatusOrderByCreatedAtAsc(AlertStatus status);

    List<AlertEntity> findByAssignedToAndStatus(String assignedTo, AlertStatus status);

}
