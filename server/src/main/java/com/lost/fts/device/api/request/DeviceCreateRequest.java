
package com.lost.fts.device.api.request;

import com.lost.fts.device.infrastructure.persistence.DevicePlatform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeviceCreateRequest(

        @NotBlank(message = "Fingerprint hash is required")
        @Size(max = 128)
        String fingerprintHash,

        @NotNull(message = "Platform is required")
        DevicePlatform platform,

        @Size(max = 100)
        String deviceModel

) {
}
