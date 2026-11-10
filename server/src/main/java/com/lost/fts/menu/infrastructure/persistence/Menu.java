package com.lost.fts.menu.infrastructure.persistence;

import com.lost.fts.permission.infrastructure.persistence.Permission;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "menus", uniqueConstraints = {
        @UniqueConstraint(name = "uk_menu_code", columnNames = "menu_code")
})
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_code", nullable = false, unique = true)
    private String menuCode;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "menu_path")
    private String menuPath;

    @Column(name = "menu_icon")
    private String menuIcon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "required_permission_id")
    private Permission requiredPermission;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Menu() {
    }

    public Long getId() {
        return id;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public void setMenuPath(String menuPath) {
        this.menuPath = menuPath;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Menu getParent() {
        return parent;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public Permission getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(Permission permission) {
        this.requiredPermission = permission;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}