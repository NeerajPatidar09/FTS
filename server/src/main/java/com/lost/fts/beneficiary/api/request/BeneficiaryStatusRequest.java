package com.lost.fts.beneficiary.api.request;

import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryStatus;
import jakarta.validation.constraints.NotNull;

public record BeneficiaryStatusRequest(
        @NotNull(message = "Beneficiary status is required") BeneficiaryStatus status) {
}
