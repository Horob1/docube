package com.horob1.auth_service.api.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateUpdatePermissionDto(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must be at most 100 characters")
    @field:Pattern(
        regexp = "^[a-z_]+:[a-z_]+$",
        message = "Name must follow the 'resource:action' format (e.g., 'user:view' or 'product_manage:create')"
    )
    val name: String,
    @field:NotBlank(message = "Description is required")
    @field:Size(max = 200, message = "description must be at most 200 characters")
    val description: String,
)