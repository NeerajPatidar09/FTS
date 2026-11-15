package com.lost.fts.security.authorization;

import com.lost.fts.investigation.infrastructure.persistence.InvestigationRepository;
import com.lost.fts.security.authorization.CurrentUserService;
import com.lost.fts.user.infrastructure.persistence.User;
import org.springframework.stereotype.Service;

@Service
public class InvestigationAccessService {

    private final CurrentUserService currentUserService;
    private final InvestigationRepository investigationRepository;

    public InvestigationAccessService(
            CurrentUserService currentUserService,
            InvestigationRepository investigationRepository
    ) {
        this.currentUserService = currentUserService;
        this.investigationRepository = investigationRepository;
    }

    public boolean canAccess(Long investigationId) {

        User user = currentUserService.getCurrentUser();

        // Replace these checks with your actual role entity properties.
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role ->
                        "ADMIN".equals(role.getRoleCode())
                );

        if (isAdmin) {
            return investigationRepository.existsById(investigationId);
        }

        Long employeeId = user.getEmployee().getId();

        boolean isInvestigator = user.getRoles().stream()
                .anyMatch(role ->
                        "INVESTIGATOR".equals(role.getRoleCode())
                );

        if (isInvestigator) {
            return investigationRepository
                    .existsAssignedToEmployee(
                            investigationId,
                            employeeId
                    );
        }

        boolean isManager = user.getRoles().stream()
                .anyMatch(role ->
                        "INVESTIGATION_MANAGER".equals(role.getRoleCode())
                );

        if (isManager) {

            Long departmentId = user.getEmployee()
                    .getDepartment()
                    .getId();

            return investigationRepository
                    .existsAssignedToDepartment(
                            investigationId,
                            departmentId
                    );
        }

        return false;
    }
}