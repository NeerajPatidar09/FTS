
package com.lost.fts.beneficiary.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lost.fts.beneficiary.api.request.BeneficiaryCreateRequest;
import com.lost.fts.beneficiary.api.request.BeneficiaryStatusRequest;
import com.lost.fts.beneficiary.api.request.BeneficiaryUpdateRequest;
import com.lost.fts.beneficiary.api.response.BeneficiaryResponse;
import com.lost.fts.beneficiary.api.response.BeneficiarySummaryResponse;

public interface BeneficiaryService {

    BeneficiaryResponse create(BeneficiaryCreateRequest request);

    BeneficiaryResponse getByReference(String beneficiaryRef);

    Page<BeneficiarySummaryResponse> getAll(
            Long accountId,
            String search,
            Pageable pageable);

    BeneficiaryResponse update(
            String beneficiaryRef,
            BeneficiaryUpdateRequest request);

    BeneficiaryResponse updateStatus(
            String beneficiaryRef,
            BeneficiaryStatusRequest request);

    void delete(String beneficiaryRef);
}
