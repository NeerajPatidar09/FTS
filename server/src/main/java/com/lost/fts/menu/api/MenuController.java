package com.lost.fts.menu.api;

import com.lost.fts.menu.api.request.*;
import com.lost.fts.menu.api.response.MenuResponse;
import com.lost.fts.menu.application.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // CREATE MENU
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public MenuResponse createMenu(@Valid @RequestBody MenuCreateRequest request) {
        return menuService.createMenu(request);
    }

    // UPDATE MENU
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public MenuResponse updateMenu(@PathVariable Long id, @Valid @RequestBody MenuUpdateRequest request) {
        return menuService.updateMenu(id, request);
    }

    // GET ALL MENUS
    @GetMapping
    @PreAuthorize("hasAuthority('MENU_VIEW')")
    public List<MenuResponse> getAllMenus() {
        return menuService.getAllMenus();
    }

    // DEACTIVATE MENU
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public void deactivateMenu(@PathVariable Long id) {
        menuService.deactivateMenu(id);
    }

    // ASSIGN MENUS TO ROLE
    @PutMapping("/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    public void assignMenusToRole(@PathVariable Long roleId, @Valid @RequestBody RoleMenuRequest request) {
        menuService.assignMenusToRole(roleId, request);
    }
}