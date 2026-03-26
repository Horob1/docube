package com.horob1.auth_service.service.query

import com.horob1.auth_service.api.dto.response.AuthorProfileResponse
import com.horob1.auth_service.api.exception.UserNotFound
import com.horob1.auth_service.domain.repository.UserRepository
import com.horob1.auth_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthorQueryHandler(
    private val userRepository: UserRepository
) {
    fun getAuthorProfile(userId: UUID): AuthorProfileResponse {
        val user = userRepository.findById(userId) ?: throw AppException(UserNotFound)
        return AuthorProfileResponse(
            id = user.id!!,
            firstName = user.firstName,
            lastName = user.lastName,
            avatar = user.avatar,
            memberSince = user.createdAt,
        )
    }
}
