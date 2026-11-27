
package com.lost.fts.beneficiary.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lost.fts.account.infrastructure.persistence.AccountEntity;
import com.lost.fts.account.infrastructure.persistence.AccountRepository;
import com.lost.fts.account.infrastructure.persistence.AccountStatus;
import com.lost.fts.beneficiary.api.request.BeneficiaryCreateRequest;
import com.lost.fts.beneficiary.api.request.BeneficiaryStatusRequest;
import com.lost.fts.beneficiary.api.request.BeneficiaryUpdateRequest;
import com.lost.fts.beneficiary.api.response.BeneficiaryResponse;
import com.lost.fts.beneficiary.api.response.BeneficiarySummaryResponse;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryEntity;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryRepository;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryStatus;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryType;

@Service
@Transactional
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryMapper beneficiaryMapper;

    public BeneficiaryServiceImpl(
            BeneficiaryRepository beneficiaryRepository,
            AccountRepository accountRepository,
            BeneficiaryMapper beneficiaryMapper) {

        this.beneficiaryRepository = beneficiaryRepository;
        this.accountRepository = accountRepository;
        this.beneficiaryMapper = beneficiaryMapper;
    }

    @Override
    public BeneficiaryResponse create(BeneficiaryCreateRequest request) {

        AccountEntity account = accountRepository
                .findByAccountRef(request.accountRef())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Beneficiaries can only be added to active accounts");
        }

        validateBeneficiaryDetails(
                request.beneficiaryType(),
                request.bankIfsc());

        validateDuplicateDestination(
                account.getId(),
                request.destinationReferenceToken());

        BeneficiaryEntity entity = new BeneficiaryEntity();

        entity.setBeneficiaryRef(
                "BEN-" + UUID.randomUUID().toString().toUpperCase());

        entity.setAccount(account);
        entity.setBeneficiaryName(request.beneficiaryName().trim());
        entity.setBeneficiaryType(request.beneficiaryType());
        entity.setDestinationReferenceToken(
                request.destinationReferenceToken().trim());
        entity.setDestinationMasked(request.destinationMasked());
        entity.setBankIfsc(normalizeIfsc(request.bankIfsc()));

        entity.setStatus(BeneficiaryStatus.PENDING);

        BeneficiaryEntity saved = beneficiaryRepository.save(entity);

        return beneficiaryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryResponse getByReference(String beneficiaryRef) {

        BeneficiaryEntity entity = findByReference(beneficiaryRef);

        return beneficiaryMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BeneficiarySummaryResponse> getAll(
            Long accountId,
            String search,
            Pageable pageable) {

        if (accountId != null && !accountRepository.existsById(accountId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Account not found");
        }

        Page<BeneficiaryEntity> page;

        if (accountId == null) {
            page = beneficiaryRepository.findAll(pageable);
        } else if (search != null && !search.isBlank()) {
            page = beneficiaryRepository
                    .findByAccountIdAndBeneficiaryNameContainingIgnoreCase(
                            accountId,
                            search.trim(),
                            pageable);
        } else {
            page = beneficiaryRepository.findByAccountId(
                    accountId,
                    pageable);
        }

        return page.map(beneficiaryMapper::toSummary);
    }

    @Override
    public BeneficiaryResponse update(
            String beneficiaryRef,
            BeneficiaryUpdateRequest request) {

        BeneficiaryEntity entity = findByReference(beneficiaryRef);

        if (entity.getStatus() == BeneficiaryStatus.DELETED
                || entity.getStatus() == BeneficiaryStatus.BLOCKED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Deleted or blocked beneficiary cannot be updated");
        }

        validateBeneficiaryDetails(
                request.beneficiaryType(),
                request.bankIfsc());

        validateDuplicateDestination(
                entity.getAccount().getId(),
                request.destinationReferenceToken(),
                entity.getId());

        entity.setBeneficiaryName(request.beneficiaryName().trim());
        entity.setBeneficiaryType(request.beneficiaryType());
        entity.setDestinationReferenceToken(
                request.destinationReferenceToken().trim());
        entity.setDestinationMasked(request.destinationMasked());
        entity.setBankIfsc(normalizeIfsc(request.bankIfsc()));

        BeneficiaryEntity saved = beneficiaryRepository.save(entity);

        return beneficiaryMapper.toResponse(saved);
    }

    @Override
    public BeneficiaryResponse updateStatus(
            String beneficiaryRef,
            BeneficiaryStatusRequest request) {

        BeneficiaryEntity entity = findByReference(beneficiaryRef);

        BeneficiaryStatus current = entity.getStatus();
        BeneficiaryStatus target = request.status();

        if (current == target) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Beneficiary already has this status");
        }

        switch (target) {

            case ACTIVE -> {
                if (current != BeneficiaryStatus.PENDING) {
                    throw invalidTransition(current, target);
                }

                if (entity.getAccount().getStatus() != AccountStatus.ACTIVE) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Cannot activate beneficiary for an inactive account");
                }

                entity.activate();
            }

            case SUSPENDED -> {
                if (current != BeneficiaryStatus.ACTIVE) {
                    throw invalidTransition(current, target);
                }

                entity.suspend();
            }

            case BLOCKED -> {
                if (current == BeneficiaryStatus.DELETED
                        || current == BeneficiaryStatus.BLOCKED) {
                    throw invalidTransition(current, target);
                }

                entity.setStatus(BeneficiaryStatus.BLOCKED);
            }

            case DELETED -> {
                if (current == BeneficiaryStatus.DELETED) {
                    throw invalidTransition(current, target);
                }

                entity.setStatus(BeneficiaryStatus.DELETED);
            }

            case PENDING -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot manually change status to PENDING");
        }

        return beneficiaryMapper.toResponse(
                beneficiaryRepository.save(entity));
    }

    @Override
    public void delete(String beneficiaryRef) {

        BeneficiaryEntity entity = findByReference(beneficiaryRef);

        if (entity.getStatus() == BeneficiaryStatus.DELETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Beneficiary is already deleted");
        }

        entity.setStatus(BeneficiaryStatus.DELETED);

        beneficiaryRepository.save(entity);
    }

    private BeneficiaryEntity findByReference(String beneficiaryRef) {

        return beneficiaryRepository
                .findByBeneficiaryRef(beneficiaryRef)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Beneficiary not found"));
    }

    private void validateDuplicateDestination(
            Long accountId,
            String destinationToken) {

        if (beneficiaryRepository
                .existsByAccountIdAndDestinationReferenceToken(
                        accountId,
                        destinationToken.trim())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Beneficiary destination already exists for this account");
        }
    }

    private void validateDuplicateDestination(
            Long accountId,
            String destinationToken,
            Long beneficiaryId) {

        if (beneficiaryRepository
                .existsByAccountIdAndDestinationReferenceTokenAndIdNot(
                        accountId,
                        destinationToken.trim(),
                        beneficiaryId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Beneficiary destination already exists for this account");
        }
    }

    private void validateBeneficiaryDetails(
            BeneficiaryType type,
            String bankIfsc) {

        if (type == BeneficiaryType.BANKACCOUNT) {

            if (bankIfsc == null || bankIfsc.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "IFSC is required for bank account beneficiaries");
            }

            if (!bankIfsc.trim().matches("^[A-Za-z]{4}0[A-Za-z0-9]{6}$")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid IFSC format");
            }
        }
    }

    private String normalizeIfsc(String bankIfsc) {

        return bankIfsc == null || bankIfsc.isBlank()
                ? null
                : bankIfsc.trim().toUpperCase();
    }

    private ResponseStatusException invalidTransition(
            BeneficiaryStatus current,
            BeneficiaryStatus target) {

        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid beneficiary status transition: "
                        + current + " -> " + target);
    }
}
