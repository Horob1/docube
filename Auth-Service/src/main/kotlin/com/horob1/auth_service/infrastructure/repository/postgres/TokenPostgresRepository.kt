package com.horob1.auth_service.infrastructure.repository.postgres

import com.horob1.auth_service.domain.model.token.Token
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TokenPostgresRepository : JpaRepository<Token, UUID> {
    fun findByToken(token: String): Token?

    fun findByUserId(userId: UUID): List<Token>

    fun deleteByUserId(userId: UUID)
}