package com.horob1.auth_service.api.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email is invalid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, max = 100, message = "Password must be 6-100 characters")
    // Bắt buộc có ít nhất 1 chữ cái và 1 số, còn ký tự đặc biệt có hay không thì không bắt buộc
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).*\$",
        message = "Password must contain at least one letter and one number"
    )
    val password: String
)