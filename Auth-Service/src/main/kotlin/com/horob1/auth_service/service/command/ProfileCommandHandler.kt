package com.horob1.auth_service.service.command

import com.horob1.auth_service.api.dto.request.ChangePasswordDto
import com.horob1.auth_service.api.exception.PasswordIsSameAsOld
import com.horob1.auth_service.api.exception.PasswordNotMatch
import com.horob1.auth_service.api.exception.UserNotFound
import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.domain.model.user.toSummary
import com.horob1.auth_service.domain.repository.UserRepository
import com.horob1.common_service.api.exception.AppException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class ProfileCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    // Update avatar

    // Update password
    fun updatePassword(id: UUID, passwordData: ChangePasswordDto): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)

        // Check old password
        if (user.password != passwordEncoder.encode(passwordData.oldPassword))
            throw AppException(PasswordNotMatch)

        if (passwordData.oldPassword == passwordData.newPassword)
            throw AppException(PasswordIsSameAsOld)

        user.password = passwordEncoder.encode(passwordData.oldPassword)
        return userRepository.save(user).toSummary()
    }

    // Toggle 2fa
    fun toggle2FA(id: UUID): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        user.is2FAEnabled = !user.is2FAEnabled
        return userRepository.save(
            user
        ).toSummary()
    }
}