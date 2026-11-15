package com.lost.fts.admin.application;

import com.lost.fts.admin.api.response.AdminDashboardResponse;
import com.lost.fts.employee.infrastructure.persistence.EmployeeRepository;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import com.lost.fts.role.infrastructure.persistence.RoleRepository;
import com.lost.fts.permission.infrastructure.persistence.PermissionRepository;
import com.lost.fts.menu.infrastructure.persistence.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lost.fts.employee.infrastructure.persistence.EmployeeStatus;
import com.lost.fts.user.infrastructure.persistence.UserStatus;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;

    public AdminServiceImpl(
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            MenuRepository menuRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public AdminDashboardResponse getDashboardSummary() {

        return new AdminDashboardResponse(
                employeeRepository.count(),
                employeeRepository.countByStatus(EmployeeStatus.ACTIVE),
                employeeRepository.countByStatus(EmployeeStatus.INACTIVE),
                userRepository.count(),
                userRepository.countByStatus(UserStatus.ACTIVE),
                userRepository.countByStatus(UserStatus.DISABLED),
                roleRepository.count(),
                permissionRepository.count(),
                menuRepository.count());
    }
}