package com.lost.fts.account.application;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lost.fts.account.api.request.AccountCreateRequest;
import com.lost.fts.account.api.response.AccountResponse;
import com.lost.fts.account.infrastructure.persistence.AccountEntity;
import com.lost.fts.account.infrastructure.persistence.AccountRepository;
import com.lost.fts.customer.infrastructure.persistence.CustomerEntity;
import com.lost.fts.customer.infrastructure.persistence.CustomerRepository;

@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {

        String accountRef = request.accountRef().trim();
        String customerRef = request.customerRef().trim();
        String currency = request.currency().trim().toUpperCase(Locale.ROOT);

        if (accountRepository.existsByAccountRef(accountRef)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Account reference already exists: " + accountRef
            );
        }

        CustomerEntity customer = customerRepository
            .findByCustomerRef(customerRef)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Customer not found: " + customerRef
            ));

        AccountEntity account = new AccountEntity();
        account.setAccountRef(accountRef);
        account.setCustomer(customer);
        account.setAccountType(request.accountType());
        account.setCurrency(currency);
        account.setOpenedOn(request.openedOn());

        AccountEntity savedAccount = accountRepository.save(account);

        return toResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountByRef(String accountRef) {

        AccountEntity account = accountRepository
            .findByAccountRef(accountRef.trim())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Account not found: " + accountRef
            ));

        return toResponse(account);
    }

    @Override
    public List<AccountResponse> getAccountsByCustomerRef(String customerRef) {

        CustomerEntity customer = customerRepository
            .findByCustomerRef(customerRef.trim())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Customer not found: " + customerRef
            ));

        return accountRepository.findByCustomerId(customer.getId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private AccountResponse toResponse(AccountEntity account) {

        return new AccountResponse(
            account.getId(),
            account.getAccountRef(),
            account.getCustomer().getCustomerRef(),
            account.getAccountType(),
            account.getStatus(),
            account.getCurrency(),
            account.getOpenedOn(),
            account.getClosedOn(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}
