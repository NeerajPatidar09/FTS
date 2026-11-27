
package com.lost.fts.device.api.response;

import java.time.Instant;

import com.lost.fts.device.infrastructure.persistence.DevicePlatform;

public record DeviceResponse(

        Long id,
        DevicePlatform platform,
        String deviceModel,
        Instant createdAt,
        Instant lastObservedAt

) {
}
