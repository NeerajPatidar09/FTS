package com.lost.fts.employee.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeUpdateRequest(

        @NotBlank @Size(max = 100) String firstName,

        @NotBlank @Size(max = 100) String lastName,

        @NotBlank @Email @Size(max = 150) String email,

        @NotBlank @Pattern(regexp = "^[0-9+() -]{7,20}$") String phone,

        @NotNull Long departmentId,

        @NotNull Long designationId

) {
}