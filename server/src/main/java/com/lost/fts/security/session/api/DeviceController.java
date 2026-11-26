package com.lost.fts.security.session.api;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.lost.fts.security.session.application.DeviceService;
import com.lost.fts.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<?> getDevices(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(deviceService.getDeviceResponses(principal.getUser().getId()));
    }

    @PostMapping("/{reference}/block")
    public ResponseEntity<?> blockDevice(@AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable String reference) {
        deviceService.blockDevice(principal.getUser().getId(), reference);
        return ResponseEntity.ok(Map.of("message", "Device blocked successfully"));
    }
}
