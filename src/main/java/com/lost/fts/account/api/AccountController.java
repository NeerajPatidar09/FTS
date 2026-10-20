package com.lost.fts.account.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lost.fts.account.api.request.AccountCreateRequest;
import com.lost.fts.account.api.response.AccountResponse;
import com.lost.fts.account.application.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
@Validated
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountCreateRequest request) {

        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/{accountRef}")
    public ResponseEntity<AccountResponse> getAccountByRef(
            @PathVariable String accountRef) {

        return ResponseEntity.ok(
            accountService.getAccountByRef(accountRef)
        );
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {

        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/by-customer/{customerRef}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(
            @PathVariable String customerRef) {

        return ResponseEntity.ok(
            accountService.getAccountsByCustomerRef(customerRef)
        );
    }
}
