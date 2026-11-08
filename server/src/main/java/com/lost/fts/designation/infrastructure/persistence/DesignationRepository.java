package com.lost.fts.designation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository
        extends JpaRepository<Designation, Long> {
}