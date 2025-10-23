package com.horob1.common_service.api.dto.response

import com.horob1.common_service.enums.UserStatus

data class UserIdentityDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val status: UserStatus,
    val is2FAEnabled: Boolean,
)