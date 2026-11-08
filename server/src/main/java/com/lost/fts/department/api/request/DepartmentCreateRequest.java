package com.lost.fts.department.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentCreateRequest(
    @NotBlank @Size(max = 30) String departmentCode,
    @NotBlank @Size(max = 100) String departmentName
) {}