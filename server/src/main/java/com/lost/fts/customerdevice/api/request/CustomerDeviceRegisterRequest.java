
package com.lost.fts.customerdevice.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerDeviceRegisterRequest(

        @NotBlank(message = "Customer reference is required")
        @Size(max = 64)
        String customerRef,

        @NotNull(message = "Device ID is required")
        Long deviceId

) {
}
