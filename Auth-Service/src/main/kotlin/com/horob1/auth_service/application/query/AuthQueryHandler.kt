package com.horob1.auth_service.application.query

import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.model.token.Token
import com.horob1.auth_service.domain.repository.AuthRepository
import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.auth_service.infrastructure.repository.RoleRepositoryImpl
import org.springframework.stereotype.Service

@Service
class AuthQueryHandler(
    private val authRepository: AuthRepository,
    private val roleRepository: RoleRepository,
) {
    fun getMyDevices(userId: String): List<Token> {
        return authRepository.findTokenByUserId(userId)
    }

    fun getMyRole(userId: String): List<Role> {
        return roleRepository.getRolesByUserId(userId)
    }
}