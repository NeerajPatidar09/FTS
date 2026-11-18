package com.lost.fts.security.password.infrastructure.persistence;

import java.time.LocalDateTime;
import com.lost.fts.user.infrastructure.persistence.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "password_history")
@Getter
@Setter
public class PasswordHistory {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;

@Column(name = "password_hash", nullable = false)
private String passwordHash;

@Column(name = "created_at", nullable = false)
private LocalDateTime createdAt;

public Long getId() {
return id;
}

public void setId(Long id) {
this.id = id;
}

public User getUser() {
return user;
}

public void setUser(User user) {
this.user = user;
}

public String getPasswordHash() {
return passwordHash;
}

public void setPasswordHash(String passwordHash) {
this.passwordHash = passwordHash;
}

public LocalDateTime getCreatedAt() {
return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
this.createdAt = createdAt;
}

}