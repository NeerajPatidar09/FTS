package com.lost.fts.security.authentication.application;

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