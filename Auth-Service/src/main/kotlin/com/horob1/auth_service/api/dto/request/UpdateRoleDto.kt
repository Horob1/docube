package com.horob1.auth_service.api.dto.request

import java.util.UUID

data class UpdateRoleDto(
    val roles: List<UUID>
)