package com.lost.fts.security.authentication.api.response;

import com.lost.fts.menu.api.response.MenuResponse;
import java.util.List;

public record LoginResponse(

    String accessToken,
    String tokenType,
    long expiresIn,
    CurrentUserResponse user,
    List<MenuResponse> menus

) {}