package com.horob1.auth_service.domain.repository

import com.horob1.auth_service.domain.model.user.User
import java.util.UUID

interface UserRepository {
    fun findByEmail(email: String): User?

    fun findById(id: UUID): User?

    fun save(user: User): User

    fun isExists(email: String): Boolean

    fun count(): Long
}