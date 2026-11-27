
package com.lost.fts.beneficiary.api.response;

import java.time.Instant;

import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryStatus;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryType;

public record BeneficiaryResponse(

        Long id,
        String beneficiaryRef,
        String accountRef,
        String customerRef,
        String beneficiaryName,
        BeneficiaryType beneficiaryType,
        String destinationMasked,
        String bankIfsc,
        BeneficiaryStatus status,
        Instant activatedAt,
        Instant lastUsedAt,
        Instant createdAt,
        Instant updatedAt

) {
}
