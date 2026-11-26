package com.lost.fts.security.session.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.http.Cookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lost.fts.security.session.api.response.DeviceResponse;
import com.lost.fts.security.session.infrastructure.persistence.DeviceStatus;
import com.lost.fts.security.session.infrastructure.persistence.UserDevice;
import com.lost.fts.security.session.infrastructure.persistence.UserDeviceRepository;
import com.lost.fts.user.infrastructure.persistence.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceServiceImpl implements DeviceService {

        private final UserDeviceRepository deviceRepository;

        @Override
        public UserDevice registerDevice(
                        User user,
                        String deviceReference,
                        HttpServletRequest request) {

                UserDevice device = deviceRepository
                                .findByDeviceReferenceAndUserId(
                                                deviceReference,
                                                user.getId())
                                .orElseGet(() -> {

                                        UserDevice newDevice = new UserDevice();

                                        newDevice.setDeviceReference(
                                                        deviceReference);

                                        newDevice.setUser(user);
                                        newDevice.setFirstSeenAt(LocalDateTime.now());
                                        newDevice.setStatus(DeviceStatus.ACTIVE);

                                        return newDevice;
                                });

                if (device.getStatus() == DeviceStatus.BLOCKED) {
                        throw new AccessDeniedException(
                                        "This device has been blocked");
                }

                device.setLastSeenAt(LocalDateTime.now());

                device.setUserAgent(
                                request.getHeader("User-Agent"));

                return deviceRepository.save(device);
        }

        @Override
        @Transactional(readOnly = true)
        public List<UserDevice> getDevices(Long userId) {

                return deviceRepository
                                .findByUserIdOrderByLastSeenAtDesc(userId);
        }

        @Override
        public void blockDevice(
                        Long userId,
                        String deviceReference) {

                UserDevice device = deviceRepository
                                .findByDeviceReferenceAndUserId(
                                                deviceReference,
                                                userId)
                                .orElseThrow(() -> new EntityNotFoundException("Device not found"));

                device.setStatus(DeviceStatus.BLOCKED);
        }

        @Override
        @Transactional(readOnly = true)
        public List<DeviceResponse> getDeviceResponses(Long userId) {

                return deviceRepository.findByUserIdOrderByLastSeenAtDesc(userId)
                                .stream()
                                .map(device -> DeviceResponse.builder()
                                                .reference(device.getDeviceReference())
                                                .deviceName(device.getDeviceName())
                                                .deviceType(device.getDeviceType())
                                                .browser(device.getBrowser())
                                                .operatingSystem(device.getOperatingSystem())
                                                .firstSeenAt(device.getFirstSeenAt())
                                                .lastSeenAt(device.getLastSeenAt())
                                                .status(device.getStatus().name())
                                                .build())
                                .toList();
        }

        @Override
        @Transactional
        public UserDevice getOrCreateDevice(
                        User user,
                        HttpServletRequest request) {

                String deviceReference = null;

                if (request.getCookies() != null) {
                        for (Cookie cookie : request.getCookies()) {
                                if ("deviceReference".equals(cookie.getName())) {
                                        deviceReference = cookie.getValue();
                                        break;
                                }
                        }
                }

                if (deviceReference != null) {
                        return deviceRepository
                                        .findByDeviceReferenceAndUserId(deviceReference, user.getId())
                                        .filter(device -> device.getStatus() == DeviceStatus.ACTIVE)
                                        .orElseThrow(() -> new IllegalArgumentException("Invalid or blocked device"));
                }

                UserDevice device = new UserDevice();
                device.setDeviceReference(UUID.randomUUID().toString());
                device.setUser(user);
                device.setDeviceName("Unknown Device");
                device.setDeviceType("UNKNOWN");
                device.setBrowser("Unknown");
                device.setOperatingSystem("Unknown");
                device.setFirstSeenAt(LocalDateTime.now());
                device.setLastSeenAt(LocalDateTime.now());
                device.setStatus(DeviceStatus.ACTIVE);
                return deviceRepository.save(device);
        }
}
