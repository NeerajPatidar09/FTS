
package com.lost.fts.customerdevice.api.response;

import java.time.Instant;

import com.lost.fts.customerdevice.infrastructure.persistence.CustomerDeviceStatus;
import com.lost.fts.device.infrastructure.persistence.DevicePlatform;

public record CustomerDeviceResponse(

        Long id,
        String customerRef,
        Long deviceId,
        DevicePlatform platform,
        String deviceModel,
        CustomerDeviceStatus status,
        Instant firstSeenAt,
        Instant lastSeenAt,
        Instant lastSuccessfulAuthAt,
        int failedAuthCount

) {
}
