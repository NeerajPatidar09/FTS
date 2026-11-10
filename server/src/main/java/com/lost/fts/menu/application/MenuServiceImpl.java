package com.lost.fts.menu.application;

import com.lost.fts.menu.api.request.*;
import com.lost.fts.menu.api.response.MenuResponse;

import com.lost.fts.menu.infrastructure.persistence.*;

import com.lost.fts.role.infrastructure.persistence.*;
import com.lost.fts.permission.infrastructure.persistence.*;

import com.lost.fts.user.infrastructure.persistence.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public MenuServiceImpl(
            MenuRepository menuRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {

        this.menuRepository = menuRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    // CREATE MENU
    @Override
    public MenuResponse createMenu(MenuCreateRequest request) {

        String code = request.menuCode().trim().toUpperCase();

        if (menuRepository.existsByMenuCodeIgnoreCase(code)) {
            throw new IllegalArgumentException(
                    "Menu code already exists");
        }

        Menu menu = new Menu();

        menu.setMenuCode(code);
        menu.setMenuName(request.menuName().trim());
        menu.setMenuPath(request.menuPath());
        menu.setMenuIcon(request.menuIcon());
        menu.setDisplayOrder(
                request.displayOrder() == null
                        ? 0 : request.displayOrder());

        if (request.parentId() != null) {

            Menu parent = menuRepository.findById(
                    request.parentId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Parent menu not found"));

            menu.setParent(parent);
        }

        if (request.requiredPermissionId() != null) {

            Permission permission =
                    permissionRepository.findById(
                            request.requiredPermissionId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Permission not found"));

            if (!permission.isActive()) {
                throw new IllegalArgumentException(
                        "Permission is inactive");
            }

            menu.setRequiredPermission(permission);
        }

        menu.setActive(true);

        return mapToResponse(menuRepository.save(menu));
    }

    // UPDATE MENU
    @Override
    public MenuResponse updateMenu(
            Long id,
            MenuUpdateRequest request) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Menu not found"));

        Menu parent = null;

        if (request.parentId() != null) {

            if (request.parentId().equals(id)) {
                throw new IllegalArgumentException(
                        "Menu cannot be its own parent");
            }

            parent = menuRepository.findById(
                    request.parentId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Parent menu not found"));

            Menu ancestor = parent;

            while (ancestor != null) {

                if (ancestor.getId().equals(id)) {
                    throw new IllegalArgumentException(
                            "Circular menu hierarchy is not allowed");
                }

                ancestor = ancestor.getParent();
            }
        }

        menu.setMenuName(request.menuName().trim());
        menu.setMenuPath(request.menuPath());
        menu.setMenuIcon(request.menuIcon());
        menu.setParent(parent);
        menu.setDisplayOrder(
                request.displayOrder() == null
                        ? 0 : request.displayOrder());

        if (request.requiredPermissionId() != null) {

            Permission permission =
                    permissionRepository.findById(
                            request.requiredPermissionId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Permission not found"));

            if (!permission.isActive()) {
                throw new IllegalArgumentException(
                        "Permission is inactive");
            }

            menu.setRequiredPermission(permission);

        } else {
            menu.setRequiredPermission(null);
        }

        return mapToResponse(menuRepository.save(menu));
    }

    // GET ALL MENUS
    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenus() {

        List<Menu> menus =
                menuRepository.findAllByOrderByDisplayOrderAsc();

        return buildMenuTree(menus);
    }

    // DEACTIVATE MENU
    @Override
    public void deactivateMenu(Long id) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Menu not found"));

        menu.setActive(false);

        menuRepository.save(menu);
    }

    // ASSIGN MENUS TO ROLE
    @Override
    public void assignMenusToRole(
            Long roleId,
            RoleMenuRequest request) {

        Role role = roleRepository
                .findWithMenusById(roleId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found"));

        if (!role.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign menus to an inactive role");
        }

        List<Menu> menus =
                menuRepository.findAllById(request.menuIds());

        if (menus.size() != request.menuIds().size()) {
            throw new IllegalArgumentException(
                    "One or more menus do not exist");
        }

        if (menus.stream().anyMatch(menu -> !menu.isActive())) {
            throw new IllegalArgumentException(
                    "Inactive menus cannot be assigned");
        }

        role.getMenus().clear();
        role.getMenus().addAll(menus);

        roleRepository.save(role);
    }

    // GET MENUS FOR LOGGED-IN USER
    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getAccessibleMenus(User user) {

        Set<String> userPermissions = new HashSet<>();

        Set<Menu> assignedMenus = new HashSet<>();

        user.getRoles().stream()
                .filter(Role::isActive)
                .forEach(role -> {

                    role.getPermissions().stream()
                            .filter(Permission::isActive)
                            .map(Permission::getPermissionCode)
                            .forEach(userPermissions::add);

                    assignedMenus.addAll(role.getMenus());
                });

        // Collect assigned active menus.
        Map<Long, Menu> visibleMenus = new HashMap<>();

        for (Menu menu : assignedMenus) {

            if (!menu.isActive()) {
                continue;
            }

            // Check the menu's required permission.
            if (menu.getRequiredPermission() != null) {

                String requiredCode =
                        menu.getRequiredPermission()
                                .getPermissionCode();

                if (!userPermissions.contains(requiredCode)) {
                    continue;
                }
            }

            visibleMenus.put(menu.getId(), menu);

            // Add active ancestors as containers.
            Menu parent = menu.getParent();

            while (parent != null) {

                if (!parent.isActive()) {
                    break;
                }

                if (parent.getRequiredPermission() != null) {

                    String requiredCode =
                            parent.getRequiredPermission()
                                    .getPermissionCode();

                    if (!userPermissions.contains(requiredCode)) {
                        break;
                    }
                }

                visibleMenus.put(parent.getId(), parent);

                parent = parent.getParent();
            }
        }

        List<Menu> menus = visibleMenus.values()
                .stream()
                .sorted(Comparator.comparing(
                        Menu::getDisplayOrder,
                        Comparator.nullsLast(
                                Comparator.naturalOrder())))
                .toList();

        return buildMenuTree(menus);
    }

    // BUILD HIERARCHICAL MENU RESPONSE
    private List<MenuResponse> buildMenuTree(
            List<Menu> menus) {

        Map<Long, List<Menu>> childrenMap =
                menus.stream()
                        .filter(menu -> menu.getParent() != null)
                        .collect(Collectors.groupingBy(
                                menu -> menu.getParent().getId()
                        ));

        return menus.stream()
                .filter(menu -> menu.getParent() == null
                        || menus.stream().noneMatch(
                                candidate ->
                                        candidate.getId().equals(
                                                menu.getParent().getId())))
                .sorted(Comparator.comparing(
                        Menu::getDisplayOrder,
                        Comparator.nullsLast(
                                Comparator.naturalOrder())))
                .map(menu -> mapTree(menu, childrenMap))
                .toList();
    }

    private MenuResponse mapTree(
            Menu menu,
            Map<Long, List<Menu>> childrenMap) {

        List<MenuResponse> children =
                childrenMap.getOrDefault(
                        menu.getId(),
                        List.of()
                )
                .stream()
                .sorted(Comparator.comparing(
                        Menu::getDisplayOrder,
                        Comparator.nullsLast(
                                Comparator.naturalOrder())))
                .map(child -> mapTree(child, childrenMap))
                .toList();

        return mapToResponse(menu, children);
    }

    private MenuResponse mapToResponse(Menu menu) {
        return mapToResponse(menu, List.of());
    }

    private MenuResponse mapToResponse(
            Menu menu,
            List<MenuResponse> children) {

        return new MenuResponse(
                menu.getId(),
                menu.getMenuCode(),
                menu.getMenuName(),
                menu.getMenuPath(),
                menu.getMenuIcon(),
                menu.getParent() == null
                        ? null : menu.getParent().getId(),
                menu.getRequiredPermission() == null
                        ? null
                        : menu.getRequiredPermission()
                                .getPermissionCode(),
                menu.getDisplayOrder(),
                menu.isActive(),
                children
        );
    }
}