package com.horob1.user_service.application.command

import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.common_service.api.exception.AppException
import com.horob1.user_service.api.exception.UserExisted
import com.horob1.user_service.domain.user.UserRepository
import com.horob1.user_service.util.toUser
import org.springframework.stereotype.Service

@Service
class InternalCallCommandHandler(
    private val userRepository: UserRepository
) {
    fun registerUser(dto: CreateUserDto): String {
        val existedUser = userRepository.findByEmail(dto.email)
        if (existedUser != null) {
            throw AppException(UserExisted)
        }
        return userRepository.save(dto.toUser()).id
    }

}