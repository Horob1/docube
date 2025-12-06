package com.horob1.auth_service.domain.model.user

import java.time.Instant
import java.util.UUID

data class UserSummary(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String = "",
    val address: String = "",
    val avatar: String = "",
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)