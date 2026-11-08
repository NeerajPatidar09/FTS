package com.lost.fts.department.api.response;

public record DepartmentResponse(

    Long id,
    String departmentCode,
    String departmentName,
    boolean active

) {
}