// package com.lost.fts.beneficiary.infrastructure.persistence;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;

// public interface BeneficiaryRepository extends JpaRepository<BeneficiaryEntity, Long> {

//     Optional<BeneficiaryEntity> findByBeneficiaryRef(String beneficiaryRef);

//     List<BeneficiaryEntity> findByAccountId(Long accountId);

//     List<BeneficiaryEntity> findByAccountIdAndStatus(Long accountId, BeneficiaryStatus status);
// }


package com.lost.fts.beneficiary.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository
        extends JpaRepository<BeneficiaryEntity, Long> {

    Optional<BeneficiaryEntity> findByBeneficiaryRef(String beneficiaryRef);

    Page<BeneficiaryEntity> findByAccountId(Long accountId, Pageable pageable);

    Page<BeneficiaryEntity> findByAccountIdAndStatus(
            Long accountId,
            BeneficiaryStatus status,
            Pageable pageable);

    Page<BeneficiaryEntity> findByAccountIdAndBeneficiaryNameContainingIgnoreCase(
            Long accountId,
            String beneficiaryName,
            Pageable pageable);

    boolean existsByAccountIdAndDestinationReferenceToken(
            Long accountId,
            String destinationReferenceToken);

    boolean existsByAccountIdAndDestinationReferenceTokenAndIdNot(
            Long accountId,
            String destinationReferenceToken,
            Long id);
}
