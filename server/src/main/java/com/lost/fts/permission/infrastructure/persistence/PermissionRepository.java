package com.lost.fts.permission.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository
        extends JpaRepository<Permission, Long> {

    Optional<Permission> findByPermissionCode(
            String permissionCode);

    boolean existsByPermissionCode(String permissionCode);
}