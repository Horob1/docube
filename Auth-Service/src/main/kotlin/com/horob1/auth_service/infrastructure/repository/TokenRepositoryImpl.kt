package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.domain.model.token.Token
import com.horob1.auth_service.domain.repository.TokenRepository
import com.horob1.auth_service.infrastructure.repository.postgres.TokenPostgresRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class TokenRepositoryImpl(
    private val tokenPostgresRepository: TokenPostgresRepository,
) : TokenRepository {
    override fun save(token: Token): Token {
        return tokenPostgresRepository.save(token)
    }

    override fun findTokenByToken(token: String): Token? {
        return tokenPostgresRepository.findByToken(token)
    }

    override fun findTokenByUserId(userId: UUID): List<Token> {
        return tokenPostgresRepository.findByUserId(userId)
    }

    override fun delete(token: Token) {
        tokenPostgresRepository.delete(token)
    }

    override fun deleteByUserId(userId: UUID) {
        return tokenPostgresRepository.deleteByUserId(userId)
    }
}