package com.horob1.auth_service.infrastructure.repository.postgres

import com.horob1.auth_service.domain.model.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserPostgresRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean
}