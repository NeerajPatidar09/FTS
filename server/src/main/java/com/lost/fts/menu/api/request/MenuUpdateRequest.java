package com.lost.fts.menu.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MenuUpdateRequest(

    @NotBlank
    @Size(max = 150)
    String menuName,

    String menuPath,

    String menuIcon,

    Long parentId,

    Long requiredPermissionId,

    Integer displayOrder

) {}