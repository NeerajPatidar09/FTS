package com.lost.fts.permission.api.response;

public record PermissionResponse(

    Long id,
    String permissionCode,
    String permissionName,
    String description,
    boolean active

) {}