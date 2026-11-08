package com.lost.fts.user.api.response;

public record UserResponse(

    Long id,
    Long employeeId,
    String employeeName,
    String username,
    String email,
    String status,
    boolean accountLocked

) {}