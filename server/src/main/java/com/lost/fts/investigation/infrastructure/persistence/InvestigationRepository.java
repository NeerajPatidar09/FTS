package com.lost.fts.investigation.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface InvestigationRepository extends JpaRepository<Investigation, Long> {

    // Investigator: only investigations assigned to them.
    @Query("""
                SELECT DISTINCT i
                FROM Investigation i
                JOIN InvestigationAssignment a
                    ON a.investigation = i
                WHERE a.employee.id = :employeeId
                  AND a.active = true
            """)
    List<Investigation> findAssignedToEmployee(@Param("employeeId") Long employeeId);

    // Manager: investigations assigned to employees in their department.
    @Query("""
                SELECT DISTINCT i
                FROM Investigation i
                JOIN InvestigationAssignment a
                    ON a.investigation = i
                WHERE a.employee.department.id = :departmentId
                  AND a.active = true
            """)
    List<Investigation> findAssignedToDepartment(@Param("departmentId") Long departmentId);

    // Investigator: check access to one investigation.
    @Query("""
                SELECT COUNT(i) > 0
                FROM Investigation i
                JOIN InvestigationAssignment a
                    ON a.investigation = i
                WHERE i.id = :investigationId
                  AND a.employee.id = :employeeId
                  AND a.active = true
            """)
    boolean existsAssignedToEmployee(
            @Param("investigationId") Long investigationId,
            @Param("employeeId") Long employeeId);

    // Manager: check access through department membership.
    @Query("""
                SELECT COUNT(i) > 0
                FROM Investigation i
                JOIN InvestigationAssignment a
                    ON a.investigation = i
                WHERE i.id = :investigationId
                  AND a.employee.department.id = :departmentId
                  AND a.active = true
            """)
    boolean existsAssignedToDepartment(
            @Param("investigationId") Long investigationId,
            @Param("departmentId") Long departmentId);
}