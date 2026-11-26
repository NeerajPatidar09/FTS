package com.lost.fts.security.session.application;

import java.util.List;

import com.lost.fts.security.session.api.response.SessionResponse;
import com.lost.fts.security.session.infrastructure.persistence.UserDevice;
import com.lost.fts.security.session.infrastructure.persistence.UserSession;
import com.lost.fts.user.infrastructure.persistence.User;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionService {

    UserSession createSession(User user, UserDevice device, HttpServletRequest request);
    List<UserSession> getActiveSessions(Long userId);
    void revokeSession(Long userId, String sessionReference);
    void revokeAllSessions(Long userId, String reason);
    boolean isSessionActive(String sessionReference);
    List<SessionResponse> getActiveSessionResponses(Long id, String sessionReference);
    // SessionResponse getSessionByReference(String sessionReference);
    void updateLastActivity(String sessionReference);

}