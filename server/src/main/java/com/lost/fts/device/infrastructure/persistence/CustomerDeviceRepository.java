package com.lost.fts.device.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lost.fts.customer.infrastructure.persistence.CustomerDeviceEntity;

public interface CustomerDeviceRepository extends JpaRepository<CustomerDeviceEntity, Long> {

    Optional<CustomerDeviceEntity> findByCustomerIdAndDeviceId(Long customerId, Long deviceId);

    List<CustomerDeviceEntity> findByCustomerId(Long customerId);

    List<CustomerDeviceEntity> findByDeviceId(Long deviceId);
}
