package com.lost.fts.security.session.application;

import org.springframework.stereotype.Service;
import com.lost.fts.riskassessment.infrastructure.persistence.RiskLevel;
import com.lost.fts.security.session.infrastructure.persistence.LoginEventType;
import com.lost.fts.security.session.infrastructure.persistence.SecurityLoginEvent;
import com.lost.fts.security.session.infrastructure.persistence.SecurityLoginEventRepository;
import com.lost.fts.user.infrastructure.persistence.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuspiciousActivityService {

    private final SecurityLoginEventRepository repository;

    public void recordEvent(User user, LoginEventType eventType, String ipAddress, String userAgent, RiskLevel riskLevel, String description) {

        SecurityLoginEvent event = new SecurityLoginEvent();
        event.setUser(user);
        event.setEventType(eventType);
        event.setIpAddress(ipAddress);
        event.setUserAgent(userAgent);
        event.setRiskLevel(riskLevel);
        event.setDescription(description);
        repository.save(event);
    }
}