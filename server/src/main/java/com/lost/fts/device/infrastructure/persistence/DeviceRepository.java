
package com.lost.fts.device.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository
        extends JpaRepository<DeviceEntity, Long> {

    Optional<DeviceEntity> findByFingerprintHash(String fingerprintHash);

    boolean existsByFingerprintHash(String fingerprintHash);

    Page<DeviceEntity> findByPlatform(
            DevicePlatform platform,
            Pageable pageable);

    Page<DeviceEntity> findByDeviceModelContainingIgnoreCase(
            String deviceModel,
            Pageable pageable);
    Page<DeviceEntity> findByPlatformAndDeviceModelContainingIgnoreCase(
        DevicePlatform platform,
        String deviceModel,
        Pageable pageable);
}
