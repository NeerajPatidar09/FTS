package com.lost.fts.role.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleCreateRequest(

    @NotBlank
    @Size(max = 50)
    String roleCode,

    @NotBlank
    @Size(max = 100)
    String roleName,

    String description

) {}