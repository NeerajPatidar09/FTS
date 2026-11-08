package com.lost.fts.department.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByDepartmentCode(String departmentCode);
    boolean existsByDepartmentCodeIgnoreCase(String code);
    boolean existsByDepartmentNameIgnoreCase(String name);
}