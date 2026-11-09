package com.lost.fts.user.application;

import com.lost.fts.user.api.request.UserCreateRequest;
import com.lost.fts.user.api.request.UserRoleRequest;
import com.lost.fts.user.api.response.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    void disableUser(Long id);
    UserResponse assignRoles(Long userId, UserRoleRequest request);
}