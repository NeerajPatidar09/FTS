package com.lost.fts.menu.api.request;

import java.util.Set;

public record RoleMenuRequest(
        @jakarta.validation.constraints.NotNull Set<Long> menuIds) {
}