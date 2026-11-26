package com.lost.fts.security.session.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityLoginEventRepository extends JpaRepository<SecurityLoginEvent, Long> {

    List<SecurityLoginEvent> findByUserIdOrderByCreatedAtDesc(Long userId);
}