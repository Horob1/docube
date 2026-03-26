package com.horob1.auth_service.service.query

import com.horob1.auth_service.api.exception.UserNotFound
import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.domain.model.user.toSummary
import com.horob1.auth_service.domain.repository.UserRepository
import com.horob1.auth_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProfileQueryHandler(
    private val userRepository: UserRepository
) {
    fun getProfile(id: UUID): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        return user.toSummary()
    }

}