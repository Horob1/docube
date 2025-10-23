package com.horob1.auth_service.api.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.*

data class CreateUpdateRoleDto(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must be at most 100 characters")
    @field:Pattern(
        regexp = "^[A-Z_]+$",
        message = "Name must only contain uppercase letters and underscores (e.g., 'SUPER_ADMIN')"
    )
    val name: String,
    @field:NotBlank(message = "Description is required")
    @field:Size(max = 200, message = "description must be at most 200 characters")
    val description: String,

    val permissions: List<UUID>,
)