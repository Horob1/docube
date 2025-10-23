package com.horob1.user_service.application.query

import com.horob1.common_service.api.exception.AppException
import com.horob1.user_service.api.exception.UserNotFound
import com.horob1.user_service.domain.user.UserRepository
import com.horob1.user_service.domain.user.UserSummary
import org.springframework.stereotype.Service

@Service
class UserQueryHandler(
    private val userRepository: UserRepository,
) {
    fun getUserSummary(id: String): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        return user.toUserSummary()
    }
}