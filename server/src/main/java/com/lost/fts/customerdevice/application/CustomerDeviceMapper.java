package com.lost.fts.customerdevice.application;

import org.springframework.stereotype.Component;

import com.lost.fts.customerdevice.api.response.CustomerDeviceResponse;
import com.lost.fts.customerdevice.infrastructure.persistence.CustomerDeviceEntity;

@Component
public class CustomerDeviceMapper {

    public CustomerDeviceResponse toResponse(CustomerDeviceEntity entity) {

        return new CustomerDeviceResponse(
                entity.getId(),
                entity.getCustomer().getCustomerRef(),
                entity.getDevice().getId(),
                entity.getDevice().getPlatform(),
                entity.getDevice().getDeviceModel(),
                entity.getStatus(),
                entity.getFirstSeenAt(),
                entity.getLastSeenAt(),
                entity.getLastSuccessfulAuthAt(),
                entity.getFailedAuthCount()
        );
    }
}
