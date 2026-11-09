package com.lost.fts.role.api;

import com.lost.fts.role.api.request.*;
import com.lost.fts.role.api.response.RoleResponse;
import com.lost.fts.role.application.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse createRole(
            @Valid @RequestBody RoleCreateRequest request) {

        return roleService.createRole(request);
    }

    @GetMapping
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public RoleResponse getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @PutMapping("/{id}/permissions")
    public RoleResponse assignPermissions(
            @PathVariable Long id,
            @Valid @RequestBody RolePermissionRequest request) {

        return roleService.assignPermissions(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateRole(@PathVariable Long id) {
        roleService.deactivateRole(id);
    }
}