package com.horob1.user_service.application.command

import com.horob1.common_service.api.exception.AppException
import com.horob1.user_service.api.dto.request.ChangePasswordDto
import com.horob1.user_service.api.exception.PasswordIsSameAsOld
import com.horob1.user_service.api.exception.PasswordNotMatch
import com.horob1.user_service.api.exception.UserNotFound
import com.horob1.user_service.domain.user.UserRepository
import com.horob1.user_service.domain.user.UserSummary
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class ProfileCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    // Update avatar

    // Update password
    fun updatePassword(id: String, passwordData: ChangePasswordDto): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)

        // Check old password
        if (user.password != passwordEncoder.encode(passwordData.oldPassword))
            throw AppException(PasswordNotMatch)

        if (passwordData.oldPassword == passwordData.newPassword)
            throw AppException(PasswordIsSameAsOld)

        return userRepository.save(user.copy(password = passwordEncoder.encode(passwordData.newPassword)))
    }

    // Toggle 2fa
    fun toggle2FA(id: String): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        return userRepository.save(
            user.copy(
                is2FAEnabled = !user.is2FAEnabled
            )
        )
    }
}