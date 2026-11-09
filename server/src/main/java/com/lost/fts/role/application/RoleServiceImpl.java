package com.lost.fts.role.application;

import com.lost.fts.role.api.request.*;
import com.lost.fts.role.api.response.RoleResponse;

import com.lost.fts.role.infrastructure.persistence.*;
import com.lost.fts.permission.infrastructure.persistence.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {

        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public RoleResponse createRole(RoleCreateRequest request) {

        String code = request.roleCode().trim()
                .toUpperCase();

        if (roleRepository.existsByRoleCode(code)) {
            throw new IllegalArgumentException(
                    "Role already exists");
        }

        Role role = new Role();

        role.setRoleCode(code);
        role.setRoleName(request.roleName().trim());
        role.setDescription(request.description());
        role.setActive(true);

        return mapToResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {

        Role role = roleRepository
                .findWithPermissionsById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found"));

        return mapToResponse(role);
    }

    @Override
    public RoleResponse assignPermissions(
            Long roleId,
            RolePermissionRequest request) {

        Role role = roleRepository
                .findWithPermissionsById(roleId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found"));

        List<Permission> permissions =
                permissionRepository.findAllById(
                        request.permissionIds());

        if (permissions.size() != request.permissionIds().size()) {
            throw new IllegalArgumentException(
                    "One or more permissions do not exist");
        }

        if (permissions.stream().anyMatch(
                permission -> !permission.isActive())) {
            throw new IllegalArgumentException(
                    "Inactive permissions cannot be assigned");
        }

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);

        return mapToResponse(roleRepository.save(role));
    }

    @Override
    public void deactivateRole(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found"));

        role.setActive(false);

        roleRepository.save(role);
    }

    private RoleResponse mapToResponse(Role role) {

        Set<String> permissions =
                role.getPermissions()
                        .stream()
                        .map(Permission::getPermissionCode)
                        .collect(Collectors.toSet());

        return new RoleResponse(
                role.getId(),
                role.getRoleCode(),
                role.getRoleName(),
                role.getDescription(),
                role.isActive(),
                permissions
        );
    }
}
