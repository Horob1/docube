package com.horob1.auth_service.api.dto.response

import java.time.Instant
import java.util.UUID

/**
 * Public profile of a document author.
 * Only exposes non-sensitive display information.
 * Intentionally omits: email, phoneNumber, address, password, totpSecret, roles, status.
 */
data class AuthorProfileResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val avatar: String,
    val memberSince: Instant?,
)
