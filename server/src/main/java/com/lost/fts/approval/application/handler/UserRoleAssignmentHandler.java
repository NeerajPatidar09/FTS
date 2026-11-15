package com.lost.fts.approval.application.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.lost.fts.approval.infrastructure.persistence.ApprovalRequest;
import com.lost.fts.approval.application.ApprovalActionHandler;
import com.lost.fts.role.infrastructure.persistence.Role;
import com.lost.fts.role.infrastructure.persistence.RoleRepository;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserRoleAssignmentHandler
        implements ApprovalActionHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleAssignmentHandler(
            UserRepository userRepository,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public String getModuleCode() {
        return "USER";
    }

    @Override
    public String getActionCode() {
        return "ASSIGN_ROLES";
    }

    @Override
    public void validate(JsonNode payload) {

        if (!payload.hasNonNull("userId")
                || !payload.has("roleIds")
                || !payload.get("roleIds").isArray()) {
            throw new IllegalArgumentException(
                    "Invalid user role assignment payload");
        }

        if (payload.get("roleIds").isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one role must be supplied");
        }

        for (JsonNode roleId : payload.get("roleIds")) {
            if (!roleId.canConvertToLong()
                    || roleId.longValue() <= 0) {
                throw new IllegalArgumentException(
                        "Invalid role ID");
            }
        }
    }

    @Override
    public void execute(
            ApprovalRequest approvalRequest,
            JsonNode payload) {

        Long userId = payload.get("userId").asLong();

        Set<Long> roleIds = new HashSet<>();

        payload.get("roleIds").forEach(
                id -> roleIds.add(id.asLong()));

        User user = userRepository
                .findWithRolesAndPermissionsById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException(
                    "Cannot assign roles to a disabled user");
        }

        Set<Role> roles = roleRepository.findAllById(roleIds)
                .stream()
                .filter(Role::isActive)
                .collect(Collectors.toSet());

        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException(
                    "One or more roles do not exist or are inactive");
        }

        // Replace the existing role assignments.
        user.getRoles().clear();
        user.getRoles().addAll(roles);

        userRepository.save(user);
    }
}