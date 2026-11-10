package com.lost.fts.security.authentication.api.response;

import java.util.List;
import java.util.Set;

public record CurrentUserResponse(

    Long userId,
    Long employeeId,
    String username,
    String email,
    String employeeName,
    Set<String> roles,
    Set<String> permissions

) {}