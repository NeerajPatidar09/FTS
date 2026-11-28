package com.lost.fts.device.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lost.fts.device.api.request.DeviceCreateRequest;
import com.lost.fts.device.api.request.DeviceUpdateRequest;
import com.lost.fts.device.api.response.DeviceResponse;
import com.lost.fts.device.infrastructure.persistence.DevicePlatform;

public interface DevicesService {

    DeviceResponse create(DeviceCreateRequest request);

    DeviceResponse getById(Long id);

    Page<DeviceResponse> getAll(
            DevicePlatform platform,
            String search,
            Pageable pageable);

    DeviceResponse update(Long id, DeviceUpdateRequest request);
}
