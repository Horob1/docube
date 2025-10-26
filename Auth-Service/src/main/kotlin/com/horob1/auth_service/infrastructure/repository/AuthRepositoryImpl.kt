package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.api.exception.InvalidToken
import com.horob1.auth_service.infrastructure.client.UserServiceClient
import com.horob1.auth_service.domain.model.token.Token
import com.horob1.auth_service.domain.repository.AuthRepository
import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.common_service.api.dto.response.UserIdentityDto
import com.horob1.common_service.api.exception.AppError
import com.horob1.common_service.api.exception.AppException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AuthRepositoryImpl(
    private val userServiceClient: UserServiceClient,
    private val tokenPostgresRepository: TokenPostgresRepository
) : AuthRepository {

    @CircuitBreaker(name = "User-Service", fallbackMethod = "findIdentityByEmailFallback")
    override fun findIdentityByEmail(email: String): UserIdentityDto? {
        return userServiceClient.getUserIdentityByEmail(email)
    }

    @CircuitBreaker(name = "User-Service", fallbackMethod = "findIdentityByUserIdFallback")
    override fun findIdentityByUserId(userId: String): UserIdentityDto? {
        return userServiceClient.getUserIdentityById(userId)
    }

    override fun saveNewRefreshToken(refreshToken: String, userId: String, ua: String): Token {
        return tokenPostgresRepository.save(
            Token(
                userId = userId,
                token = refreshToken,
                ua = ua
            )
        )
    }

    override fun saveToken(token: Token): Token {
        return tokenPostgresRepository.save(
            token
        )
    }

    @CircuitBreaker(name = "User-Service", fallbackMethod = "createUserFallback")
    override fun createUser(body: CreateUserDto): String {
        return userServiceClient.createUser(body)
    }

    override fun findTokenById(id: UUID): Token? {
        return tokenPostgresRepository.findById(id).orElse(null)
    }

    override fun findToken(token: String): Token? {
        return tokenPostgresRepository.findByToken(token)
    }

    override fun deleteToken(token: String) {
        tokenPostgresRepository.deleteByToken(token)
    }

    override fun findTokenByUserId(userId: String): List<Token> {
        return tokenPostgresRepository.findByUserId(userId)
    }

    override fun deleteTokenByUserId(userId: String) {
        tokenPostgresRepository.deleteByUserId(userId)
    }

    override fun updateToken(oldToken: String, newToken: String): Token {
        val existed = tokenPostgresRepository.findByToken(oldToken) ?: throw AppException(InvalidToken)
        existed.token = newToken
        return tokenPostgresRepository.save(
            existed
        )
    }

    @Suppress("unused")
    private fun createUserFallback(body: CreateUserDto, t: Throwable) {
        throw AppException(AppError.InternalServerError)
    }

    @Suppress("unused")
    private fun findIdentityByEmailFallback(email: String, t: Throwable) {
        throw AppException(AppError.InternalServerError)
    }

    @Suppress("unused")
    private fun findIdentityByUserIdFallback(userId: String, t: Throwable) {
        throw AppException(AppError.InternalServerError)
    }
}