
package com.lost.fts.customerdevice.api.request;

import com.lost.fts.customerdevice.infrastructure.persistence.CustomerDeviceStatus;

import jakarta.validation.constraints.NotNull;

public record CustomerDeviceStatusRequest(

        @NotNull(message = "Device status is required")
        CustomerDeviceStatus status

) {
}
