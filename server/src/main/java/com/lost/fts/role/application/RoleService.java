package com.lost.fts.role.application;

import com.lost.fts.role.api.request.RoleCreateRequest;
import com.lost.fts.role.api.request.RolePermissionRequest;
import com.lost.fts.role.api.response.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleCreateRequest request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse assignPermissions(
            Long roleId,
            RolePermissionRequest request);

    void deactivateRole(Long id);
}