
package com.lost.fts.beneficiary.application;

import org.springframework.stereotype.Component;

import com.lost.fts.beneficiary.api.response.BeneficiaryResponse;
import com.lost.fts.beneficiary.api.response.BeneficiarySummaryResponse;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryEntity;

@Component
public class BeneficiaryMapper {

    public BeneficiaryResponse toResponse(BeneficiaryEntity entity) {

        return new BeneficiaryResponse(
                entity.getId(),
                entity.getBeneficiaryRef(),
                entity.getAccount().getAccountRef(),
                entity.getAccount().getCustomer().getCustomerRef(),
                entity.getBeneficiaryName(),
                entity.getBeneficiaryType(),
                entity.getDestinationMasked(),
                entity.getBankIfsc(),
                entity.getStatus(),
                entity.getActivatedAt(),
                entity.getLastUsedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public BeneficiarySummaryResponse toSummary(
            BeneficiaryEntity entity) {

        return new BeneficiarySummaryResponse(
                entity.getBeneficiaryRef(),
                entity.getBeneficiaryName(),
                entity.getBeneficiaryType(),
                entity.getDestinationMasked(),
                entity.getStatus()
        );
    }
}
