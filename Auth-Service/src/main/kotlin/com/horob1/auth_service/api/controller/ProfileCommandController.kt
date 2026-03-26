package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.request.ChangePasswordDto
import com.horob1.auth_service.api.dto.request.OTPDto
import com.horob1.auth_service.api.dto.response.TotpSetupResponse
import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.service.command.ProfileCommandHandler
import com.horob1.auth_service.shared.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/profile")
class ProfileCommandController(
    private val profileCommandHandler: ProfileCommandHandler,
) {
    @PutMapping("/avatar", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateAvatar(
        @RequestPart("file") file: MultipartFile,
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            HttpStatus.OK,
            message = "Avatar updated successfully",
            data = profileCommandHandler.updateAvatar(userId, file)
        )
        return ResponseEntity(apiResponse, HttpStatus.OK)
    }

    @PostMapping("/password")
    fun changePassword(
        @Valid @RequestBody data: ChangePasswordDto,
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            HttpStatus.OK,
            message = "Change password successfully",
            data = profileCommandHandler.updatePassword(
                userId,
                data
            )
        )
        return ResponseEntity(apiResponse, HttpStatus.OK)
    }

    // Setup 2FA - Step 1: Get secret and QR code URL
    @PostMapping("/2fa/setup")
    fun setup2FA(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<ApiResponse<TotpSetupResponse>> {
        val apiResponse = ApiResponse.success(
            HttpStatus.OK,
            message = "2FA setup initiated. Scan the QR code with Google Authenticator.",
            data = profileCommandHandler.setup2FA(userId)
        )
        return ResponseEntity(apiResponse, HttpStatus.OK)
    }

    // Setup 2FA - Step 2: Verify TOTP code and enable 2FA
    @PostMapping("/2fa/verify")
    fun verify2FASetup(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody data: OTPDto,
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            HttpStatus.OK,
            message = "2FA enabled successfully",
            data = profileCommandHandler.verify2FASetup(userId, data.otp)
        )
        return ResponseEntity(apiResponse, HttpStatus.OK)
    }

    // Disable 2FA - Requires valid TOTP code
    @PostMapping("/2fa/disable")
    fun disable2FA(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody data: OTPDto,
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            HttpStatus.OK,
            message = "2FA disabled successfully",
            data = profileCommandHandler.disable2FA(userId, data.otp)
        )
        return ResponseEntity(apiResponse, HttpStatus.OK)
    }
}