package com.horob1.auth_service.api.dto.response

data class LoginResponseDto(
    val accessToken: String,
    val accessTokenType: String,
    val refreshToken: String? = null,
    val clientId: String? = null,
)