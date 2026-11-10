package com.lost.fts.role.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.lost.fts.permission.infrastructure.persistence.Permission;
import java.util.HashSet;
import java.util.Set;
import com.lost.fts.menu.infrastructure.persistence.Menu;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_code", columnNames = "role_code")
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_code", nullable = false, unique = true)
    private String roleCode;

    @Column(name = "role_name", nullable = false)

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "role_id", "permission_id" }))
    private Set<Permission> permissions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_menus", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "role_id", "menu_id" }))
    private Set<Menu> menus = new HashSet<>();

    private String roleName;

    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Menu> getMenus() {
        return menus;
    }

    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }

}