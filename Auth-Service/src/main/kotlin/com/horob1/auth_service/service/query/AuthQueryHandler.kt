package com.horob1.auth_service.service.query

import com.horob1.auth_service.api.exception.UserNotFound
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.model.token.Token
import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.auth_service.domain.repository.TokenRepository
import com.horob1.auth_service.domain.repository.UserRepository
import com.horob1.auth_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthQueryHandler(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
) {
    fun getMyDevices(userId: UUID): List<Token> {
        return tokenRepository.findTokenByUserId(userId)
    }

    fun getMyRoles(userId: UUID): List<Role> {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        return user.roles.toList()
    }

}