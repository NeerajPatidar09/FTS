package com.lost.fts.employee.api.response;
import java.time.LocalDate;

public record EmployeeResponse(

    Long id,
    String employeeCode,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Long departmentId,
    String departmentName,
    Long designationId,
    String designationName,
    LocalDate joiningDate,
    String status

) {}