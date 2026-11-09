package com.lost.fts.user.api;

import com.lost.fts.user.api.request.UserCreateRequest;
import com.lost.fts.user.api.request.UserRoleRequest;
import com.lost.fts.user.api.response.UserResponse;
import com.lost.fts.user.application.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody UserCreateRequest request) {

        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {

        return userService.getUserById(id);
    }

    @PatchMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableUser(@PathVariable Long id) {

        userService.disableUser(id);
    }

    @PutMapping("/{id}/roles")
    public UserResponse assignRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleRequest request) {

        return userService.assignRoles(id, request);
    }
}