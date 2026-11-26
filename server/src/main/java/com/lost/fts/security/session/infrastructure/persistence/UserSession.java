package com.lost.fts.security.session.infrastructure.persistence;

import java.time.LocalDateTime;
import com.lost.fts.user.infrastructure.persistence.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_reference", nullable = false, unique = true)
    private String sessionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private UserDevice device;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    private LocalDateTime createdAt;

    private LocalDateTime lastActivityAt;

    private LocalDateTime expiresAt;

    private LocalDateTime revokedAt;

    private String revokedReason;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;
}