package com.lost.fts.role.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RolePermissionRequest(

    @NotNull
    @NotEmpty
    Set<Long> permissionIds

) {}