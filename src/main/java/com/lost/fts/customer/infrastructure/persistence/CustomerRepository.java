package com.lost.fts.customer.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity,Long>{
    Optional<CustomerEntity> findByCustomerRef(String customerRef);
    boolean existsByCustomerRef(String customerRef);
}
