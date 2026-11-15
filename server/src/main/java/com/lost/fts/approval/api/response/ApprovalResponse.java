package com.lost.fts.approval.api.response;

import java.time.Instant;

public record ApprovalResponse(
        Long id,
        String requestReference,
        String moduleCode,
        String actionCode,
        Long entityId,
        Long makerUserId,
        Long checkerUserId,
        String requestPayload,
        String status,
        String makerComments,
        String checkerComments,
        Instant submittedAt,
        Instant decidedAt) {
}