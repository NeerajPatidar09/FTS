package com.lost.fts.approval.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lost.fts.approval.api.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import com.lost.fts.approval.api.response.ApprovalResponse;
import com.lost.fts.approval.infrastructure.persistence.*;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

        private final ApprovalRequestRepository approvalRepository;
        private final UserRepository userRepository;
        private final ObjectMapper objectMapper;

        private final Map<String, ApprovalActionHandler> handlers;

        public ApprovalServiceImpl(
                        ApprovalRequestRepository approvalRepository,
                        UserRepository userRepository,
                        ObjectMapper objectMapper,
                        List<ApprovalActionHandler> handlerList) {
                this.approvalRepository = approvalRepository;
                this.userRepository = userRepository;
                this.objectMapper = objectMapper;

                this.handlers = handlerList.stream().collect(
                                Collectors.toMap(
                                                handler -> handler.getModuleCode()
                                                                + ":" + handler.getActionCode(),
                                                Function.identity()));
        }

        @Override
        @Transactional
        public ApprovalResponse submit(
                        ApprovalSubmitRequest request,
                        String username) {

                User maker = findUser(username);

                String key = request.moduleCode()
                                + ":" + request.actionCode();

                ApprovalActionHandler handler = handlers.get(key);

                if (handler == null) {
                        throw new IllegalArgumentException(
                                        "Unsupported approval operation");
                }

                try {
                        JsonNode payload = objectMapper.readTree(
                                        request.requestPayload());

                        handler.validate(payload);

                        ApprovalRequest approval = new ApprovalRequest();

                        approval.setRequestReference(
                                        "APR-" + UUID.randomUUID());

                        approval.setModuleCode(request.moduleCode());
                        approval.setActionCode(request.actionCode());
                        approval.setEntityId(request.entityId());
                        approval.setMakerUserId(maker.getId());
                        approval.setRequestPayload(
                                        objectMapper.writeValueAsString(payload));
                        approval.setMakerComments(request.makerComments());
                        approval.setStatus(ApprovalStatus.PENDING);

                        return toResponse(
                                        approvalRepository.save(approval));

                } catch (Exception ex) {
                        if (ex instanceof IllegalArgumentException) {
                                throw (IllegalArgumentException) ex;
                        }

                        throw new IllegalArgumentException(
                                        "Invalid approval payload", ex);
                }
        }

        @Override
        @Transactional
        public ApprovalResponse decide(
                        Long approvalId,
                        ApprovalDecisionRequest request,
                        String checkerUsername) {

                User checker = findUser(checkerUsername);

                ApprovalRequest approval = approvalRepository
                                .findByIdForUpdate(approvalId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Approval request not found"));

                if (approval.getStatus() != ApprovalStatus.PENDING) {
                        throw new IllegalStateException(
                                        "This request has already been processed");
                }

                if (approval.getMakerUserId().equals(checker.getId())) {
                        throw new IllegalArgumentException(
                                        "Maker cannot approve their own request");
                }

                if ("REJECT".equalsIgnoreCase(request.decision())) {

                        approval.reject(
                                        checker.getId(),
                                        request.comments());

                        return toResponse(approval);
                }

                if (!"APPROVE".equalsIgnoreCase(request.decision())) {
                        throw new IllegalArgumentException(
                                        "Decision must be APPROVE or REJECT");
                }

                String key = approval.getModuleCode()
                                + ":" + approval.getActionCode();

                ApprovalActionHandler handler = handlers.get(key);

                if (handler == null) {
                        throw new IllegalStateException(
                                        "Approval handler not found");
                }

                try {
                        JsonNode payload = objectMapper.readTree(
                                        approval.getRequestPayload());

                        // Revalidate before execution.
                        handler.validate(payload);

                        // Execute the actual business operation.
                        handler.execute(approval, payload);

                        approval.approve(
                                        checker.getId(),
                                        request.comments());

                        return toResponse(approval);

                } catch (Exception ex) {
                        // The entire transaction should roll back if the
                        // business operation cannot be executed.
                        throw new IllegalStateException(
                                        "Unable to approve request", ex);
                }
        }

        @Override
        @Transactional(readOnly = true)
        public List<ApprovalResponse> getPendingRequests() {

                return approvalRepository
                                .findByStatusOrderBySubmittedAtAsc(
                                                ApprovalStatus.PENDING)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ApprovalResponse> getMyRequests(String username) {

                User maker = findUser(username);

                return approvalRepository
                                .findByMakerUserIdOrderBySubmittedAtDesc(
                                                maker.getId())
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        private User findUser(String username) {

                return userRepository.findByUsernameIgnoreCase(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }

        private ApprovalResponse toResponse(ApprovalRequest a) {

                return new ApprovalResponse(
                                a.getId(),
                                a.getRequestReference(),
                                a.getModuleCode(),
                                a.getActionCode(),
                                a.getEntityId(),
                                a.getMakerUserId(),
                                a.getCheckerUserId(),
                                a.getRequestPayload(),
                                a.getStatus().name(),
                                a.getMakerComments(),
                                a.getCheckerComments(),
                                a.getSubmittedAt(),
                                a.getDecidedAt());
        }
}