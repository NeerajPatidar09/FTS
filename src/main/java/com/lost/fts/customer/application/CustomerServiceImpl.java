package com.lost.fts.customer.application;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lost.fts.customer.api.request.CustomerCreateRequest;
import com.lost.fts.customer.api.response.CustomerResponse;
import com.lost.fts.customer.infrastructure.persistence.CustomerEntity;
import com.lost.fts.customer.infrastructure.persistence.CustomerRepository;

@Service
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {

        String customerRef = request.customerRef().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (customerRepository.existsByCustomerRef(customerRef)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Customer reference already exists: " + customerRef);
        }

        String firstName = request.firstName().trim();
        String lastName = request.lastName().trim();
        String fullName = firstName + " " + lastName;

        if (fullName.length() > 40) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Full name must not exceed 40 characters");
        }

        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerRef(customerRef);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setFullName(fullName);
        customer.setEmail(email);

        CustomerEntity savedCustomer = customerRepository.save(customer);

        return toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerByRef(String customerRef) {

        CustomerEntity customer = customerRepository
                .findByCustomerRef(customerRef.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found: " + customerRef));

        return toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CustomerResponse toResponse(CustomerEntity customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerRef(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }
}
