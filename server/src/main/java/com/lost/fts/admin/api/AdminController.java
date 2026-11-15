package com.lost.fts.admin.api;

import com.lost.fts.admin.api.response.AdminDashboardResponse;
import com.lost.fts.admin.application.AdminService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ADMIN_DASHBOARD_VIEW')")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboardSummary();
    }
}