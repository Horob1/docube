package com.horob1.user_service.domain.user

import java.time.Instant

data class UserSummary(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String = "",
    val address: String = "",
    val avatar: String = "",
    val createdAt: Instant? = null,
)