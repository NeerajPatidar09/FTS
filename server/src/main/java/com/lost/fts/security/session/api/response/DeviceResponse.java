package com.lost.fts.security.session.api.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceResponse {

    private String reference;
    private String deviceName;
    private String deviceType;
    private String browser;
    private String operatingSystem;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
    private String status;
}
