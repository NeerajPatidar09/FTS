package com.lost.fts.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmployee_Id(Long employeeId);

    @EntityGraph(attributePaths = { "roles", "roles.permissions" })
    Optional<User> findWithRolesAndPermissionsById(Long id);

    @EntityGraph(attributePaths = {
            "employee",
            "roles",
            "roles.permissions",
            "roles.menus",
            "roles.menus.requiredPermission",
            "roles.menus.parent",
            "roles.menus.parent.requiredPermission"
    })
    Optional<User> findWithRolesAndPermissionsByUsernameIgnoreCase(String username);
}