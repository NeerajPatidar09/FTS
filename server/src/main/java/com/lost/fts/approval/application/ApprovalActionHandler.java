package com.lost.fts.approval.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.lost.fts.approval.infrastructure.persistence.ApprovalRequest;

public interface ApprovalActionHandler {

    String getModuleCode();

    String getActionCode();

    void validate(JsonNode payload);

    void execute(ApprovalRequest approvalRequest, JsonNode payload);
}