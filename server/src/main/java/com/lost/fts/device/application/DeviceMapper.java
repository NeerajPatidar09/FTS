
package com.lost.fts.device.application;

import org.springframework.stereotype.Component;

import com.lost.fts.device.api.response.DeviceResponse;
import com.lost.fts.device.infrastructure.persistence.DeviceEntity;

@Component
public class DeviceMapper {

    public DeviceResponse toResponse(DeviceEntity entity) {

        return new DeviceResponse(
                entity.getId(),
                entity.getPlatform(),
                entity.getDeviceModel(),
                entity.getCreatedAt(),
                entity.getLastObservedAt()
        );
    }
}
