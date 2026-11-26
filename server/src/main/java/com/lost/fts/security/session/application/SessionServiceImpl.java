package com.lost.fts.security.session.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lost.fts.security.session.api.response.SessionResponse;
import com.lost.fts.security.session.infrastructure.persistence.SessionStatus;
import com.lost.fts.security.session.infrastructure.persistence.UserDevice;
import com.lost.fts.security.session.infrastructure.persistence.UserSession;
import com.lost.fts.security.session.infrastructure.persistence.UserSessionRepository;
import com.lost.fts.user.infrastructure.persistence.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final UserSessionRepository sessionRepository;
    // private final SessionMapper sessionMapper;

    @Value("${app.session.expiration-hours:12}")
    private long expirationHours;

    @Override
    @Transactional
    public UserSession createSession(User user, UserDevice device, HttpServletRequest request) {

        LocalDateTime now = LocalDateTime.now();
        UserSession session = new UserSession();
        session.setSessionReference(UUID.randomUUID().toString());
        session.setUser(user);
        session.setDevice(device);
        session.setIpAddress(request.getRemoteAddr());
        session.setUserAgent(request.getHeader("User-Agent"));
        session.setCreatedAt(now);
        session.setLastActivityAt(now);
        session.setExpiresAt(now.plusHours(expirationHours));
        session.setStatus(SessionStatus.ACTIVE);
        return sessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getActiveSessions(Long userId) {

        return sessionRepository
                .findByUserIdAndStatusOrderByLastActivityAtDesc(
                        userId,
                        SessionStatus.ACTIVE);
    }

    @Override
    public void revokeSession(Long userId, String sessionReference) {
        UserSession session = sessionRepository.findBySessionReferenceAndUserId(sessionReference, userId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        session.setStatus(SessionStatus.REVOKED);
        session.setRevokedAt(LocalDateTime.now());
        session.setRevokedReason("USER_REQUEST");
    }

    @Override
    public void revokeAllSessions(Long userId, String reason) {
        sessionRepository.revokeAllUserSessions(userId, LocalDateTime.now(), reason, SessionStatus.ACTIVE,
                SessionStatus.REVOKED);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionActive(String sessionReference) {

        return sessionRepository
                .findBySessionReferenceAndStatus(
                        sessionReference,
                        SessionStatus.ACTIVE)
                .filter(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Override
    public void updateLastActivity(String sessionReference) {
        sessionRepository.updateLastActivity(
                sessionReference,
                LocalDateTime.now(),
                SessionStatus.ACTIVE);
    }

    // @Override
    // @Transactional(readOnly = true)
    // public SessionResponse getSessionByReference(String sessionReference) {
    // UserSession session =
    // sessionRepository.findBySessionReference(sessionReference).orElseThrow();//.orElseThrow(()
    // -> new ResourceNotFoundException("Session not found"));
    // return sessionMapper.toResponse(session);
    // }

    @Override
    public List<SessionResponse> getActiveSessionResponses(Long id, String sessionReference) {
        List<UserSession> sessions = sessionRepository.findByUserIdAndStatus(id, SessionStatus.ACTIVE);
        return sessions.stream()
                .map(session -> new SessionResponse(
                        session.getSessionReference(),
                        session.getDevice() != null ? session.getDevice().getDeviceName() : "Unknown Device",
                        session.getDevice() != null ? session.getDevice().getBrowser() : "Unknown",
                        session.getDevice() != null ? session.getDevice().getOperatingSystem() : "Unknown",
                        session.getIpAddress(),
                        session.getCreatedAt(),
                        session.getLastActivityAt(),
                        session.getExpiresAt(),
                        session.getSessionReference().equals(sessionReference)))
                .toList();
    }

}
