package com.horob1.auth_service.api.dto.request

import java.util.UUID

data class LogoutDevicesDto(
    val deviceIds: List<UUID>,
)