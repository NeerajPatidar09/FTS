package com.lost.fts.alert.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertEvidenceRepository extends JpaRepository<AlertEvidenceEntity, Long> {

    List<AlertEvidenceEntity> findByAlertId(Long alertId);
}
