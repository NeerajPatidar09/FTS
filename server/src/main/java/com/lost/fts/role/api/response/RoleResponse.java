package com.lost.fts.role.api.response;

import java.util.Set;

public record RoleResponse(

    Long id,
    String roleCode,
    String roleName,
    String description,
    boolean active,
    Set<String> permissions

) {}