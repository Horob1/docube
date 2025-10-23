package com.horob1.common_service.api.dto.request

import com.horob1.common_service.enums.UserStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateUserDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email is invalid")
    val email: String,

    @field:NotBlank(message = "First name is required")
    @field:Size(max = 50, message = "First name must be at most 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(max = 50, message = "Last name must be at most 50 characters")
    val lastName: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, max = 100, message = "Password must be 6-100 characters")
    // Bắt buộc có ít nhất 1 chữ cái và 1 số, còn ký tự đặc biệt có hay không thì không bắt buộc
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).*\$",
        message = "Password must contain at least one letter and one number"
    )
    val password: String,

    @field:Pattern(
        regexp = "^\\+?[0-9]{7,15}\$",
        message = "Phone number is invalid"
    )
    val phoneNumber: String = "",

    @field:Size(max = 255, message = "Address must be at most 255 characters")
    val address: String = "",

    val status: UserStatus = UserStatus.PENDING,
)