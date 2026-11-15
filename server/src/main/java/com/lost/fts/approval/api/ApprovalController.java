package com.lost.fts.approval.api;

import com.lost.fts.approval.api.request.*;
import com.lost.fts.approval.api.response.ApprovalResponse;
import com.lost.fts.approval.application.ApprovalService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('APPROVAL_SUBMIT')")
    public ApprovalResponse submit(
            @Valid @RequestBody ApprovalSubmitRequest request,
            Authentication authentication
    ) {
        return approvalService.submit(
                request,
                authentication.getName()
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('APPROVAL_VIEW')")
    public List<ApprovalResponse> getPendingRequests() {
        return approvalService.getPendingRequests();
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasAuthority('APPROVAL_VIEW')")
    public List<ApprovalResponse> getMyRequests(
            Authentication authentication
    ) {
        return approvalService.getMyRequests(
                authentication.getName()
        );
    }

    @PostMapping("/{approvalId}/decision")
    @PreAuthorize("hasAuthority('APPROVAL_DECIDE')")
    public ApprovalResponse decide(
            @PathVariable Long approvalId,
            @Valid @RequestBody ApprovalDecisionRequest request,
            Authentication authentication
    ) {
        return approvalService.decide(
                approvalId,
                request,
                authentication.getName()
        );
    }
}