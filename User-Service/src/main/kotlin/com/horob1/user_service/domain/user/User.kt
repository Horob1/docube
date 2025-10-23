package com.horob1.user_service.domain.user

import com.horob1.common_service.enums.UserStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "users")
data class User(
    @Id var id: String? = null,
    @Indexed(unique = true)
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phoneNumber: String = "",
    val address: String = "",
    val avatar: String = "",
    @Indexed(background = true)
    val status: UserStatus = UserStatus.PENDING,
    val previousStatus: UserStatus? = null,
    val is2FAEnabled: Boolean = false,

    // Timestamps
    @CreatedDate
    var createdAt: Instant? = null,

    @LastModifiedDate
    var updatedAt: Instant? = null
) {
    fun toUserSummary(): UserSummary = UserSummary(
        id = id!!,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        address = address,
        avatar = avatar,
        createdAt = createdAt,
    )
}