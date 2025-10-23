package com.horob1.user_service.application.command

import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.common_service.api.exception.AppException
import com.horob1.common_service.enums.UserStatus
import com.horob1.user_service.api.dto.request.UpdateUserDto
import com.horob1.user_service.api.exception.UserExisted
import com.horob1.user_service.api.exception.UserIsActive
import com.horob1.user_service.api.exception.UserNotFound
import com.horob1.user_service.domain.user.UserRepository
import com.horob1.user_service.domain.user.UserSummary
import com.horob1.user_service.util.toUser
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun create(createData: CreateUserDto): UserSummary {
        val isExisted = userRepository.isExisted(createData.email)
        if (isExisted) {
            throw AppException(UserExisted)
        }

        return userRepository.save(
            createData.toUser().copy(
                password = passwordEncoder.encode(createData.password)
            )
        )
    }

    fun update(id: String, updateData: UpdateUserDto): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        val newUser = user.copy(
            firstName = updateData.firstName,
            lastName = updateData.lastName,
            phoneNumber = updateData.phoneNumber,
            address = updateData.address,
            is2FAEnabled = updateData.is2FAEnabled
        )
        return userRepository.save(newUser)
    }

    fun banUser(id: String) {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        userRepository.save(user.copy(previousStatus = user.status, status = UserStatus.BAN))
    }

    fun unbanUser(id: String) {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        if (user.status != UserStatus.BAN) {
            throw AppException(UserIsActive)
        }
        userRepository.save(
            user.copy(
                status = user.previousStatus ?: UserStatus.PENDING,
                previousStatus = UserStatus.BAN
            )
        )
    }
}