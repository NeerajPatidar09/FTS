package com.lost.fts.transaction.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lost.fts.account.infrastructure.persistence.AccountEntity;
import com.lost.fts.account.infrastructure.persistence.AccountRepository;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryEntity;
import com.lost.fts.beneficiary.infrastructure.persistence.BeneficiaryRepository;
import com.lost.fts.customer.infrastructure.persistence.CustomerDeviceEntity;
import com.lost.fts.device.infrastructure.persistence.CustomerDeviceRepository;
import com.lost.fts.messaging.infrastructure.persistence.OutboxEventEntity;
import com.lost.fts.messaging.infrastructure.persistence.OutboxEventRepository;
import com.lost.fts.transaction.api.dto.TransactionCreateRequest;
import com.lost.fts.transaction.api.dto.TransactionCreateResult;
import com.lost.fts.transaction.api.dto.TransactionResponse;
import com.lost.fts.transaction.infrastructure.persistence.TransactionEntity;
import com.lost.fts.transaction.infrastructure.persistence.TransactionRepository;

import tools.jackson.databind.ObjectMapper;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final String TRANSACTIONTOPIC = "fts.transaction.received.v1";

    private static final String EVENTTYPE = "TRANSACTIONRECEIVED";

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final CustomerDeviceRepository customerDeviceRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public TransactionServiceImpl(
        TransactionRepository transactionRepository,AccountRepository accountRepository,BeneficiaryRepository beneficiaryRepository,
        CustomerDeviceRepository customerDeviceRepository,OutboxEventRepository outboxEventRepository,ObjectMapper objectMapper) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.customerDeviceRepository = customerDeviceRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public TransactionCreateResult ingest(TransactionCreateRequest request) {

        if (request == null) {
            throw badRequest("Transaction request is required");
        }

        String sourceSystem = request.sourceSystem().trim();
        String externalRef = normalize(request.externalTransactionRef());

        if (externalRef != null) {
            var existing = transactionRepository.findBySourceSystemAndExternalTransactionRef(sourceSystem, externalRef);

            if (existing.isPresent()) {
                TransactionEntity transaction = existing.get();

                if (!matchesExisting(transaction, request)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,"External transaction reference was reused with different transaction data");
                }

                return new TransactionCreateResult(TransactionResponse.from(transaction),true);
            }
        }

        String sourceRef = normalize(request.sourceAccountRef());
        String destinationRef = normalize(request.destinationAccountRef());
        String beneficiaryRef = normalize(request.beneficiaryRef());

        if (sourceRef == null && destinationRef == null) {
            throw badRequest("At least one internal account reference is required");
        }

        if ((beneficiaryRef != null || request.customerDeviceId() != null) && sourceRef == null) {
            throw badRequest("Beneficiary and device associations require a source account");
        }

        AccountEntity sourceAccount = findAccountOrNull(sourceRef, "Source account");
        AccountEntity destinationAccount = findAccountOrNull(destinationRef, "Destination account");

        if (sourceAccount != null && destinationAccount != null && sourceAccount.getId().equals(destinationAccount.getId())) {
            throw badRequest("Source and destination accounts must be different");
        }

        BeneficiaryEntity beneficiary = null;

        if (beneficiaryRef != null) {
            beneficiary = beneficiaryRepository.findByBeneficiaryRef(beneficiaryRef).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Beneficiary not found"));

            if (!beneficiary.getAccount().getId().equals(sourceAccount.getId())) {
                throw badRequest("Beneficiary does not belong to the source account");
            }
        }

        CustomerDeviceEntity customerDevice = null;

        if (request.customerDeviceId() != null) {
            customerDevice = customerDeviceRepository.findById(request.customerDeviceId()).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"Customer-device association not found"));

            if (!customerDevice.getCustomer().getId().equals(sourceAccount.getCustomer().getId())) {
                throw badRequest("Device association does not belong to the source account holder");
            }
        }

        String transactionRef = "TXN-" + UUID.randomUUID().toString().replace("-", "");

        TransactionEntity transaction = new TransactionEntity(
                transactionRef,sourceAccount,destinationAccount,beneficiary,customerDevice,sourceSystem,externalRef,
                request.transactionType(),request.channel(),request.amount(),request.currency(),
                normalize(request.counterpartyReferenceToken()),normalize(request.counterpartyMasked()),
                request.occurredAt()
        );

        TransactionEntity saved = transactionRepository.save(transaction);

        /*
         * The transaction and outbox row commit or roll back together.
         * A separate publisher will send committed outbox events to Kafka.
         */
        String eventId = UUID.randomUUID().toString();

        TransactionReceivedEvent event = new TransactionReceivedEvent(
                eventId,EVENTTYPE,saved.getId(),saved.getTransactionRef(),
                sourceAccount == null ? null : sourceAccount.getId(),
                destinationAccount == null
                        ? null : destinationAccount.getId(),
                saved.getAmount(), saved.getCurrency(), saved.getTransactionType(), saved.getChannel(), saved.getOccurredAt()
        );

        OutboxEventEntity outboxEvent = new OutboxEventEntity(
                eventId,
                TRANSACTIONTOPIC,
                "TRANSACTION",
                saved.getTransactionRef(),
                EVENTTYPE,
                objectMapper.valueToTree(event),
                Instant.now()
        );

        outboxEventRepository.save(outboxEvent);

        return new TransactionCreateResult(
                TransactionResponse.from(saved),
                false
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getByTransactionRef(String transactionRef) {

        if (transactionRef == null || transactionRef.isBlank()) {
            throw badRequest("Transaction reference is required");
        }

        TransactionEntity transaction = transactionRepository
                .findByTransactionRef(transactionRef.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found"
                ));

        return TransactionResponse.from(transaction);
    }

    private AccountEntity findAccountOrNull(String accountRef,String description) {

        if (accountRef == null) {
            return null;
        }

        return accountRepository.findByAccountRef(accountRef).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,description + " not found"));
    }

    private boolean matchesExisting(
            TransactionEntity existing,
            TransactionCreateRequest request) {

        return same(existing.getSourceSystem(), request.sourceSystem())
                && same(existing.getTransactionType(), request.transactionType())
                && same(existing.getChannel(), request.channel())
                && existing.getAmount().compareTo(request.amount()) == 0
                && same(existing.getCurrency(), request.currency())
                && same(existing.getOccurredAt(), request.occurredAt())
                && same(
                        accountRef(existing.getSourceAccount()),
                        normalize(request.sourceAccountRef()))
                && same(
                        accountRef(existing.getDestinationAccount()),
                        normalize(request.destinationAccountRef()))
                && same(
                        beneficiaryRef(existing.getBeneficiary()),
                        normalize(request.beneficiaryRef()))
                && same(
                        deviceAssociationId(existing.getCustomerDevice()),
                        request.customerDeviceId())
                && same(
                        existing.getCounterpartyReferenceToken(),
                        normalize(request.counterpartyReferenceToken()))
                && same(
                        existing.getCounterpartyMasked(),
                        normalize(request.counterpartyMasked()));
    }

    private String accountRef(AccountEntity account) {
        return account == null ? null : account.getAccountRef();
    }

    private String beneficiaryRef(BeneficiaryEntity beneficiary) {
        return beneficiary == null ? null : beneficiary.getBeneficiaryRef();
    }

    private Long deviceAssociationId(CustomerDeviceEntity device) {
        return device == null ? null : device.getId();
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static boolean same(Object first, Object second) {
        return java.util.Objects.equals(first, second);
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}