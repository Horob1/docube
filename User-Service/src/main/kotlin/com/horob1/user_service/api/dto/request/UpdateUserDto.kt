package com.horob1.user_service.api.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserDto(
    @field:NotBlank(message = "First name is required")
    @field:Size(max = 50, message = "First name must be at most 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(max = 50, message = "Last name must be at most 50 characters")
    val lastName: String,

    @field:Pattern(
        regexp = "^\\+?[0-9]{7,15}\$",
        message = "Phone number is invalid"
    )
    val phoneNumber: String = "",

    @field:Size(max = 255, message = "Address must be at most 255 characters")
    val address: String = "",

    val is2FAEnabled: Boolean = false,
    )