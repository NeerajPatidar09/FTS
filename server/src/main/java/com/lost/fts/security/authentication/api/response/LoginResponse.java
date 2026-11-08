package com.lost.fts.security.authentication.api.response;

public record LoginResponse(

    String accessToken,

    String tokenType,

    long expiresIn

) {}