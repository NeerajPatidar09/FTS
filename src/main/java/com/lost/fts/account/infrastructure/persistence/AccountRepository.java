package com.lost.fts.account.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountRef(String accountRef);

    List<AccountEntity> findByCustomerId(Long customerId);

    boolean existsByAccountRef(String accountRef);
}