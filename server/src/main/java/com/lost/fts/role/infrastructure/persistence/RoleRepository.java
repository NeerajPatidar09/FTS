package com.lost.fts.role.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleCode(String roleCode);
    boolean existsByRoleCode(String roleCode);
    @EntityGraph(attributePaths = "menus")
    Optional<Role> findWithMenusById(Long id);
    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findWithPermissionsById(Long id);
    @EntityGraph(attributePaths = "permissions")
    List<Role> findAll();
}