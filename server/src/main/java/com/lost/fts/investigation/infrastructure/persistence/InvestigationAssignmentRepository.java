package com.lost.fts.investigation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvestigationAssignmentRepository extends JpaRepository<InvestigationAssignment, Long> {

    List<InvestigationAssignment> findByEmployeeIdAndActiveTrue(Long employeeId);

    boolean existsByInvestigationIdAndEmployeeIdAndActiveTrue(Long investigationId, Long employeeId);
}