package com.lost.fts.menu.api.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record RoleMenuRequest(
        @jakarta.validation.constraints.NotNull Set<Long> menuIds) {
}