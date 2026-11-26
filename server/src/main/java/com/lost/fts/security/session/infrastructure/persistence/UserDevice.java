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
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_devices")
@Getter
@Setter
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String deviceReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String deviceName;

    private String deviceType;

    private String browser;

    private String operatingSystem;

    private LocalDateTime firstSeenAt;

    private LocalDateTime lastSeenAt;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private String  userAgent;
}
