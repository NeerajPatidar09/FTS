package com.lost.fts.admin.api.response;

public record AdminDashboardResponse(
        long totalEmployees,
        long activeEmployees,
        long inactiveEmployees,

        long totalUsers,
        long activeUsers,
        long disabledUsers,

        long totalRoles,
        long totalPermissions,
        long totalMenus) {
}