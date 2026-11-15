package com.lost.fts.investigation.application;

import com.lost.fts.investigation.infrastructure.persistence.InvestigationRepository;
import com.lost.fts.investigation.infrastructure.persistence.Investigation;
import com.lost.fts.security.authorization.InvestigationAccessService;
import com.lost.fts.security.authorization.CurrentUserService;
import com.lost.fts.user.infrastructure.persistence.User;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Service
public class InvestigationServiceImpl implements InvestigationService {

        private final InvestigationRepository investigationRepository;
        private final InvestigationAccessService accessService;
        private final CurrentUserService currentUserService;

        public InvestigationServiceImpl(
                        InvestigationRepository investigationRepository,
                        InvestigationAccessService accessService, CurrentUserService currentUserService) {
                this.investigationRepository = investigationRepository;
                this.accessService = accessService;
                this.currentUserService = currentUserService;
        }

        public Investigation getById(Long id) {

                if (!accessService.canAccess(id)) {
                        throw new AccessDeniedException(
                                        "You are not authorized to access this investigation");
                }

                return investigationRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Investigation not found"));
        }

        public List<Investigation> getAccessibleInvestigations() {

                User user = currentUserService.getCurrentUser();

                boolean isAdmin = user.getRoles().stream()
                                .anyMatch(role -> "ADMIN".equals(role.getRoleCode()));

                if (isAdmin) {
                        return investigationRepository.findAll();
                }

                Long employeeId = user.getEmployee().getId();

                boolean isInvestigator = user.getRoles().stream()
                                .anyMatch(role -> "INVESTIGATOR".equals(role.getRoleCode()));

                if (isInvestigator) {
                        return investigationRepository
                                        .findAssignedToEmployee(employeeId);
                }

                boolean isManager = user.getRoles().stream()
                                .anyMatch(role -> "INVESTIGATION_MANAGER".equals(role.getRoleCode()));

                if (isManager) {
                        Long departmentId = user.getEmployee()
                                        .getDepartment()
                                        .getId();

                        return investigationRepository
                                        .findAssignedToDepartment(departmentId);
                }

                throw new AccessDeniedException(
                                "You are not authorized to view investigations");
        }
}