package com.horob1.auth_service.api.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class OTPDto(
    @field:NotBlank(message = "Otp is required")
    @field:Pattern(regexp = "[0-9]+", message = "Invalid otp format")
    val otp: String,
) {

}