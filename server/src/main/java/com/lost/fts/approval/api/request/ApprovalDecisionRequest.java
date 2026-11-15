package com.lost.fts.approval.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApprovalDecisionRequest(

        @NotBlank String decision,

        @Size(max = 1000) String comments) {
}