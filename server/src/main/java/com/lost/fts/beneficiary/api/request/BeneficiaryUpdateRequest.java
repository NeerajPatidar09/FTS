package com.lost.fts.beneficiary.api.request;

import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BeneficiaryUpdateRequest(

        @NotBlank(message = "Beneficiary name is required") @Size(max = 150) String beneficiaryName,

        @NotNull(message = "Beneficiary type is required") BeneficiaryType beneficiaryType,

        @NotBlank(message = "Destination reference token is required") @Size(max = 128) String destinationReferenceToken,

        @Size(max = 50) String destinationMasked,

        @Size(max = 11) String bankIfsc

) {
}
