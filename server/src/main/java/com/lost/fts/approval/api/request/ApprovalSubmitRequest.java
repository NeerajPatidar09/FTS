package com.lost.fts.approval.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApprovalSubmitRequest(

        @NotBlank @Size(max = 50) String moduleCode,

        @NotBlank @Size(max = 50) String actionCode,

        Long entityId,

        @NotBlank String requestPayload,

        @Size(max = 1000) String makerComments) {
}