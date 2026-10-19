package com.lost.fts.beneficiary.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<BeneficiaryEntity, Long> {

    Optional<BeneficiaryEntity> findByBeneficiaryRef(String beneficiaryRef);

    List<BeneficiaryEntity> findByAccountId(Long accountId);

    List<BeneficiaryEntity> findByAccountIdAndStatus(Long accountId, BeneficiaryStatus status);
}
