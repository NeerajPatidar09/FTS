package com.lost.fts.account.application;

import java.util.List;

import com.lost.fts.account.api.request.AccountCreateRequest;
import com.lost.fts.account.api.response.AccountResponse;

public interface AccountService {

    AccountResponse createAccount(AccountCreateRequest request);
    AccountResponse getAccountByRef(String accountRef);
    List<AccountResponse> getAccountsByCustomerRef(String customerRef);
    List<AccountResponse> getAllAccounts();
}
