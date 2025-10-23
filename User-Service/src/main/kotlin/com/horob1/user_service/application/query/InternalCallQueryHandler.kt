package com.horob1.user_service.application.query

import com.horob1.common_service.api.dto.response.UserIdentityDto
import com.horob1.user_service.domain.user.UserRepository
import org.springframework.stereotype.Service


@Service
class InternalCallQueryHandler(
    private val userRepository: UserRepository,
) {
    fun getUserByEmail(email: String): UserIdentityDto? {
        val user = userRepository.findByEmail(email)
        return if (user != null) {
            UserIdentityDto(
                id = user.id!!,
                email = user.email,
                password = user.password,
                status = user.status,
                is2FAEnabled = user.is2FAEnabled,
                firstName = user.firstName,
                lastName = user.lastName,
            )
        } else null
    }

    fun getUserById(userId: String): UserIdentityDto? {
        val user = userRepository.findById(userId)
        return if (user != null) {
            UserIdentityDto(
                id = user.id!!,
                email = user.email,
                password = user.password,
                status = user.status,
                is2FAEnabled = user.is2FAEnabled,
                firstName = user.firstName,
                lastName = user.lastName,
            )
        } else null
    }
}