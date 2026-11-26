package com.lost.fts.security.session.application;

import java.util.List;

import com.lost.fts.security.session.api.response.DeviceResponse;
import com.lost.fts.security.session.infrastructure.persistence.UserDevice;
import com.lost.fts.user.infrastructure.persistence.User;

import jakarta.servlet.http.HttpServletRequest;

public interface DeviceService {

    UserDevice registerDevice(User user, String deviceReference, HttpServletRequest request);
    List<UserDevice> getDevices(Long userId);
    void blockDevice(Long userId, String deviceReference);
    List<DeviceResponse> getDeviceResponses(Long userId);
    UserDevice getOrCreateDevice(User user, HttpServletRequest request);
}