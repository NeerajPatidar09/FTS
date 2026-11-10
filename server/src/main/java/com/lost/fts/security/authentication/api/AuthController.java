package com.lost.fts.security.authentication.api;

import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.api.response.LoginResponse;
import com.lost.fts.security.authentication.application.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.lost.fts.security.authentication.api.response.UserContextResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

   

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @GetMapping("/me")
    public UserContextResponse getCurrentUser(Authentication authentication) {
        return authenticationService.getCurrentUser(authentication.getName());
    }
}