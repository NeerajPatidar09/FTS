
package com.lost.fts.device.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lost.fts.device.api.request.DeviceCreateRequest;
import com.lost.fts.device.api.request.DeviceUpdateRequest;
import com.lost.fts.device.api.response.DeviceResponse;
import com.lost.fts.device.infrastructure.persistence.DeviceEntity;
import com.lost.fts.device.infrastructure.persistence.DevicePlatform;
import com.lost.fts.device.infrastructure.persistence.DeviceRepository;

@Service
@Transactional
public class DevicesServiceImpl implements DevicesService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    public DevicesServiceImpl(
            DeviceRepository deviceRepository,
            DeviceMapper deviceMapper) {

        this.deviceRepository = deviceRepository;
        this.deviceMapper = deviceMapper;
    }

    @Override
    public DeviceResponse create(DeviceCreateRequest request) {

        String fingerprintHash = request.fingerprintHash().trim();

        if (deviceRepository.existsByFingerprintHash(fingerprintHash)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Device fingerprint already registered");
        }

        DeviceEntity entity = new DeviceEntity();

        entity.setFingerprintHash(fingerprintHash);
        entity.setPlatform(request.platform());
        entity.setDeviceModel(normalize(request.deviceModel()));

        return deviceMapper.toResponse(deviceRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceResponse getById(Long id) {

        return deviceMapper.toResponse(findDevice(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceResponse> getAll(DevicePlatform platform, String search, Pageable pageable) {

        Page<DeviceEntity> page;
        if (platform != null && search != null && !search.isBlank()) {
            page = deviceRepository.findByPlatformAndDeviceModelContainingIgnoreCase(platform, search.trim(), pageable);
        } else if (platform != null) {
            page = deviceRepository.findByPlatform(platform, pageable);
        } else if (search != null && !search.isBlank()) {
            page = deviceRepository.findByDeviceModelContainingIgnoreCase(search.trim(), pageable);
        } else {
            page = deviceRepository.findAll(pageable);
        }
        return page.map(deviceMapper::toResponse);
    }

    @Override
    public DeviceResponse update(
            Long id,
            DeviceUpdateRequest request) {

        DeviceEntity entity = findDevice(id);

        entity.setPlatform(request.platform());
        entity.setDeviceModel(normalize(request.deviceModel()));

        return deviceMapper.toResponse(deviceRepository.save(entity));
    }

    private DeviceEntity findDevice(Long id) {

        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Device not found"));
    }

    private String normalize(String value) {

        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}
