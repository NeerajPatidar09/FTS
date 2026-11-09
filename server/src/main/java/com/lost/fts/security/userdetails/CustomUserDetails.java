package com.lost.fts.security.userdetails;

import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserStatus;
import com.lost.fts.role.infrastructure.persistence.Role;
import com.lost.fts.permission.infrastructure.persistence.Permission;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;

    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {

        this.user = user;

        Set<GrantedAuthority> grantedAuthorities =
                new HashSet<>();

        for (Role role : user.getRoles()) {

            if (!role.isActive()) {
                continue;
            }

            grantedAuthorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role.getRoleCode()
                    )
            );

            for (Permission permission : role.getPermissions()) {

                if (permission.isActive()) {

                    grantedAuthorities.add(
                            new SimpleGrantedAuthority(
                                    permission.getPermissionCode()
                            )
                    );
                }
            }
        }

        this.authorities = Set.copyOf(grantedAuthorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isAccountLocked()
                && user.getStatus() != UserStatus.LOCKED;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public User getUser() {
        return user;
    }
}