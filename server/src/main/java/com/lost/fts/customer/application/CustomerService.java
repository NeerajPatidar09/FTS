package com.lost.fts.customer.application;

import java.util.List;

import com.lost.fts.customer.api.request.CustomerCreateRequest;
import com.lost.fts.customer.api.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerCreateRequest request);
    CustomerResponse getCustomerByRef(String customerRef);
    List<CustomerResponse> getAllCustomers();
}
