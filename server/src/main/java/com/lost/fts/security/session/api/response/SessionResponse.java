package com.lost.fts.security.session.api.response;

import java.time.LocalDateTime;

public record SessionResponse(
    String sessionReference,
    String deviceName,
    String browser,
    String operatingSystem,
    String ipAddress,
    LocalDateTime createdAt,
    LocalDateTime lastActivityAt,
    LocalDateTime expiresAt,
    boolean currentSession
) {}
