package com.horob1.auth_service.api.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ForgotPasswordDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email is invalid")
    val email: String,
    )