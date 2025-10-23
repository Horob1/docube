package com.horob1.auth_service.api.dto.request

import jakarta.validation.constraints.NotBlank

data class RefreshTokenDto(
    @field:NotBlank(message = "Token is required")
    val token: String
)