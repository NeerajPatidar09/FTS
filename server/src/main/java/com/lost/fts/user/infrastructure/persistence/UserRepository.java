package com.lost.fts.user.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

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

    long count();

    long countByStatus(UserStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") Long id);

    Optional<User> findByEmail(String email);


}