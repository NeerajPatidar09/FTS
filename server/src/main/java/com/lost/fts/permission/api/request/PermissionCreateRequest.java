package com.lost.fts.permission.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionCreateRequest(

    @NotBlank
    @Size(max = 100)
    String permissionCode,

    @NotBlank
    @Size(max = 150)
    String permissionName,

    String description

) {}