
package com.lost.fts.customerdevice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDeviceRepository
        extends JpaRepository<CustomerDeviceEntity, Long> {

    Optional<CustomerDeviceEntity> findByCustomerIdAndDeviceId(
            Long customerId,
            Long deviceId);

    List<CustomerDeviceEntity> findByCustomerId(Long customerId);

    List<CustomerDeviceEntity> findByDeviceId(Long deviceId);

    Page<CustomerDeviceEntity> findByCustomerId(
            Long customerId,
            Pageable pageable);

    Page<CustomerDeviceEntity> findByCustomerIdAndStatus(
            Long customerId,
            CustomerDeviceStatus status,
            Pageable pageable);

    boolean existsByCustomerIdAndDeviceId(
            Long customerId,
            Long deviceId);
}
