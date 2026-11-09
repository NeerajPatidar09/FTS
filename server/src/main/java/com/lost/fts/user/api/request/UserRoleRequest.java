package com.lost.fts.user.api.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserRoleRequest(

    @NotEmpty
    Set<Long> roleIds

) {}