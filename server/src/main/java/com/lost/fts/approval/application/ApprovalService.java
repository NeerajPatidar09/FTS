package com.lost.fts.approval.application;

import com.lost.fts.approval.api.request.ApprovalSubmitRequest;
import com.lost.fts.approval.api.request.ApprovalDecisionRequest;
import com.lost.fts.approval.api.response.ApprovalResponse;

import java.util.List;

public interface ApprovalService {

    ApprovalResponse submit(ApprovalSubmitRequest request, String username);

    ApprovalResponse decide(Long approvalId, ApprovalDecisionRequest request, String checkerUsername);

    List<ApprovalResponse> getPendingRequests();

    List<ApprovalResponse> getMyRequests(String username);
}