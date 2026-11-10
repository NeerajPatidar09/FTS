package com.lost.fts.security.authentication.application;
/*
import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.api.response.LoginResponse;
import com.lost.fts.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;

        public AuthenticationService(
                        AuthenticationManager authenticationManager,
                        JwtService jwtService) {

                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
        }

        public LoginResponse login(LoginRequest request) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.username(),
                                                request.password()));

                String token = jwtService.generateToken(
                                (org.springframework.security.core.userdetails.UserDetails) authentication
                                                .getPrincipal());

                return new LoginResponse(
                                token,
                                "Bearer",
                                jwtService.getExpiration());
        }
}
*/

import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.api.response.*;
import com.lost.fts.security.jwt.JwtService;
import com.lost.fts.security.userdetails.CustomUserDetails;
import com.lost.fts.menu.application.MenuService;
import com.lost.fts.user.infrastructure.persistence.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import com.lost.fts.user.infrastructure.persistence.UserRepository;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final MenuService menuService;
        private final UserRepository userRepository;

        public AuthenticationService(
                        AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        MenuService menuService, UserRepository userRepository) {

                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
                this.menuService = menuService;
                this.userRepository = userRepository;
        }

        public LoginResponse login(LoginRequest request) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.username(),
                                                request.password()));

                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                User user = userDetails.getUser();

                Set<String> roles = userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> authority.startsWith("ROLES_"))
                                .map(authority -> authority.substring(6))
                                .collect(Collectors.toSet());

                Set<String> permissions = userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> !authority.startsWith("ROLES_"))
                                .collect(Collectors.toSet());

                Long employeeId = user.getEmployee() == null
                                ? null
                                : user.getEmployee().getId();

                String employeeName = user.getEmployee() == null
                                ? null
                                : user.getEmployee().getFirstName()
                                                + " "
                                                + user.getEmployee().getLastName();

                CurrentUserResponse currentUser = new CurrentUserResponse(
                                user.getId(),
                                employeeId,
                                user.getUsername(),
                                user.getEmail(),
                                employeeName,
                                roles,
                                permissions);

                String token = jwtService.generateToken(userDetails);

                return new LoginResponse(
                                token,
                                "Bearer",
                                jwtService.getExpiration(),
                                currentUser,
                                menuService.getAccessibleMenus(user));
        }

        public UserContextResponse getCurrentUser(
                        String username) {

                User user = userRepository.findWithRolesAndPermissionsByUsernameIgnoreCase(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                CustomUserDetails userDetails = new CustomUserDetails(user);

                Set<String> roles = userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(value -> value.startsWith("ROLES_"))
                                .map(value -> value.substring(6))
                                .collect(Collectors.toSet());
                Set<String> permissions = userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(value -> !value.startsWith("ROLES_"))
                                .collect(Collectors.toSet());

                Long employeeId = user.getEmployee() == null
                                ? null
                                : user.getEmployee().getId();

                String employeeName = user.getEmployee() == null
                                ? null
                                : user.getEmployee().getFirstName()
                                                + " " + user.getEmployee().getLastName();

                CurrentUserResponse currentUser = new CurrentUserResponse(
                                user.getId(),
                                employeeId,
                                user.getUsername(),
                                user.getEmail(),
                                employeeName,
                                roles,
                                permissions);

                return new UserContextResponse(
                                currentUser,
                                menuService.getAccessibleMenus(user));
        }
}