
package com.lost.fts.beneficiary.api.response;

import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryStatus;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryType;

public record BeneficiarySummaryResponse(

        String beneficiaryRef,
        String beneficiaryName,
        BeneficiaryType beneficiaryType,
        String destinationMasked,
        BeneficiaryStatus status

) {
}
