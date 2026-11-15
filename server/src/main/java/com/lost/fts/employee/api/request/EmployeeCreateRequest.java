package com.lost.fts.employee.api.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EmployeeCreateRequest(

    @NotBlank
    String employeeCode,

    @NotBlank
    String firstName,

    @NotBlank
    String lastName,

    @NotBlank
    @Email
    String email,
    
    @NotBlank @Pattern(regexp = "^[0-9+() -]{7,20}$")
    String phone,

    @NotNull
    Long departmentId,

    @NotNull
    Long designationId,

    LocalDate joiningDate

) {}