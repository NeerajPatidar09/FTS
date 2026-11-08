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

    String phoneNumber,

    @NotNull
    Long departmentId,

    @NotNull
    Long designationId,

    LocalDate joiningDate

) {}