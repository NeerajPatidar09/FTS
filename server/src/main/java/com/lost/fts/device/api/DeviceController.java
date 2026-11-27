
package com.lost.fts.device.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.lost.fts.device.api.request.DeviceCreateRequest;
import com.lost.fts.device.api.request.DeviceUpdateRequest;
import com.lost.fts.device.api.response.DeviceResponse;
import com.lost.fts.device.application.DeviceService;
import com.lost.fts.device.infrastructure.persistence.DevicePlatform;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/devices")
@Validated
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> create(
            @Valid @RequestBody DeviceCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deviceService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(deviceService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DeviceResponse>> getAll(
            @RequestParam(required = false) DevicePlatform platform,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(
                deviceService.getAll(platform, search, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DeviceUpdateRequest request) {

        return ResponseEntity.ok(
                deviceService.update(id, request));
    }
}
