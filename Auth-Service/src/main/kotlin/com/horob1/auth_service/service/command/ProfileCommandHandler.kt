package com.horob1.auth_service.service.command

import com.horob1.auth_service.api.dto.request.ChangePasswordDto
import com.horob1.auth_service.api.dto.response.TotpSetupResponse
import com.horob1.auth_service.api.exception.PasswordIsSameAsOld
import com.horob1.auth_service.api.exception.PasswordNotMatch
import com.horob1.auth_service.api.exception.TwoFAAlreadyEnabled
import com.horob1.auth_service.api.exception.TwoFANotEnabled
import com.horob1.auth_service.api.exception.UserNotFound
import com.horob1.auth_service.api.exception.WrongOtp
import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.domain.model.user.toSummary
import com.horob1.auth_service.domain.repository.UserRepository
import com.horob1.auth_service.service.CloudinaryService
import com.horob1.auth_service.service.TotpService
import com.horob1.auth_service.shared.exception.AppException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID


@Service
class ProfileCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val totpService: TotpService,
    private val cloudinaryService: CloudinaryService,
) {
    // Update avatar
    fun updateAvatar(id: UUID, file: MultipartFile): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        val imageUrl = cloudinaryService.uploadAvatar(file, id.toString())
        user.avatar = imageUrl
        return userRepository.save(user).toSummary()
    }

    // Update password
    fun updatePassword(id: UUID, passwordData: ChangePasswordDto): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)

        // Check old password
        if (!passwordEncoder.matches(passwordData.oldPassword, user.password))
            throw AppException(PasswordNotMatch)

        if (passwordData.oldPassword == passwordData.newPassword)
            throw AppException(PasswordIsSameAsOld)

        user.password = passwordEncoder.encode(passwordData.newPassword)
        return userRepository.save(user).toSummary()
    }

    // Setup 2FA - Step 1: Generate secret and QR code URL
    fun setup2FA(id: UUID): TotpSetupResponse {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)

        if (user.is2FAEnabled) {
            throw AppException(TwoFAAlreadyEnabled)
        }

        val secret = totpService.generateSecret()
        user.totpSecret = secret
        userRepository.save(user)

        val qrCodeUrl = totpService.generateQrCodeUrl(user.email, secret)
        return TotpSetupResponse(secret = secret, qrCodeUrl = qrCodeUrl)
    }

    // Setup 2FA - Step 2: Verify TOTP code and enable 2FA
    fun verify2FASetup(id: UUID, code: String): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        val secret = user.totpSecret ?: throw AppException(TwoFANotEnabled)

        if (!totpService.verifyCode(secret, code)) {
            throw AppException(WrongOtp)
        }

        user.is2FAEnabled = true
        return userRepository.save(user).toSummary()
    }

    // Disable 2FA - Requires valid TOTP code
    fun disable2FA(id: UUID, code: String): UserSummary {
        val user = userRepository.findById(id) ?: throw AppException(UserNotFound)
        val secret = user.totpSecret ?: throw AppException(TwoFANotEnabled)

        if (!totpService.verifyCode(secret, code)) {
            throw AppException(WrongOtp)
        }

        user.is2FAEnabled = false
        user.totpSecret = null
        return userRepository.save(user).toSummary()
    }
}