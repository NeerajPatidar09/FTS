package com.lost.fts.employee.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmployeeCodeContainingIgnoreCase(
            String firstName,
            String lastName,
            String employeeCode,
            Pageable pageable);
}