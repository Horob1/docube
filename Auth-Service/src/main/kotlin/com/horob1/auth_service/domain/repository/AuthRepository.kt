package com.horob1.auth_service.domain.repository

import com.horob1.auth_service.domain.model.token.Token
import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.common_service.api.dto.response.UserIdentityDto
import org.springframework.web.bind.annotation.RequestBody

interface AuthRepository {
    fun findIdentityByEmail(email: String): UserIdentityDto?

    fun findIdentityByUserId(userId: String): UserIdentityDto?

    fun saveNewRefreshToken(refreshToken: String, userId: String, ua: String): Token

    fun saveToken(token: Token): Token

    fun createUser(@RequestBody body: CreateUserDto): String

    fun findToken(token: String): Token?

    fun deleteToken(token: String)

    fun findTokenByUserId(userId: String): List<Token>

    fun deleteTokenByUserId(userId: String)

    fun updateToken(oldToken: String, newToken: String): Token
}