package com.lost.fts.security.session.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeviceRepository
        extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByDeviceReferenceAndUserId(
        String deviceReference,
        Long userId
    );

    List<UserDevice> findByUserIdOrderByLastSeenAtDesc(
        Long userId
    );
}