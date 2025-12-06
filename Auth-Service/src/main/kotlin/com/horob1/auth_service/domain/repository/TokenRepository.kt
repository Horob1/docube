package com.horob1.auth_service.domain.repository

import com.horob1.auth_service.domain.model.token.Token
import java.util.UUID

interface TokenRepository {
    fun save(token: Token): Token

    fun findTokenByToken(token: String): Token?

    fun findTokenByUserId(userId: UUID): List<Token>

    fun delete(token: Token)

    fun deleteByUserId(userId: UUID)

}