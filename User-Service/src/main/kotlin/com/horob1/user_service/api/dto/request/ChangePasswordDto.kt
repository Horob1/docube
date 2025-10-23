package com.horob1.user_service.api.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangePasswordDto(
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 6, max = 100, message = "New password must be 6-100 characters")
    val newPassword: String,

    @field:NotBlank(message = "Old password is required")
    @field:Size(min = 6, max = 100, message = "Old password must be 6-100 characters")
    val oldPassword: String
)