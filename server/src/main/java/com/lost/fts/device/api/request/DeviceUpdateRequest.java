
package com.lost.fts.device.api.request;

import com.lost.fts.device.infrastructure.persistence.DevicePlatform;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeviceUpdateRequest(

        @NotNull(message = "Platform is required")
        DevicePlatform platform,

        @Size(max = 100)
        String deviceModel

) {
}
