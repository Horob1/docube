package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.request.ForgotPasswordDto
import com.horob1.auth_service.api.dto.request.GoogleAuthDto
import com.horob1.auth_service.api.dto.request.LoginDto
import com.horob1.auth_service.api.dto.request.LogoutDevicesDto
import com.horob1.auth_service.api.dto.request.OTPDto
import com.horob1.auth_service.api.dto.request.RefreshTokenDto
import com.horob1.auth_service.api.dto.request.RegisterDto
import com.horob1.auth_service.api.dto.request.ResetPasswordDto
import com.horob1.auth_service.api.dto.response.LoginResponseDto
import com.horob1.auth_service.service.command.AuthCommandHandler
import com.horob1.common_service.api.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auth")
class AuthCommandController(
    private val authCommandHandler: AuthCommandHandler
) {
    // Login
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody data: LoginDto,
        @RequestHeader("User-Agent") userAgent: String
    ): ResponseEntity<ApiResponse<LoginResponseDto>> {
        val apiResponse = ApiResponse(
            status = HttpStatus.OK,
            data = authCommandHandler.login(
                email = data.email,
                password = data.password,
                ua = userAgent
            ),
            message = "Successfully logged in!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Login with Google
    @PostMapping("/google")
    fun google(
        @Valid @RequestBody data: GoogleAuthDto,
        @RequestHeader("User-Agent") userAgent: String
    ): ResponseEntity<ApiResponse<LoginResponseDto>> {
        val apiResponse = ApiResponse(
            status = HttpStatus.OK,
            data = authCommandHandler.loginGoogle(
                token = data.token,
                ua = userAgent
            ),
            message = "Successfully logged in!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Register
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody data: RegisterDto,
    ): ResponseEntity<ApiResponse<Nothing>> {
        authCommandHandler.registerUser(
            data
        )
        val apiResponse = ApiResponse<Nothing>(
            status = HttpStatus.OK,
            message = "Successfully registered!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Verify email
    @PostMapping("/verify-email")
    fun verifyEmail(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody data: OTPDto
    ): ResponseEntity<ApiResponse<Nothing>> {
        authCommandHandler.verifyEmail(
            userId = userId,
            otp = data.otp,
        )
        val apiResponse = ApiResponse<Nothing>(
            status = HttpStatus.OK,
            message = "Successfully verified!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Send Email Verify
    @PostMapping("/send-verification-email")
    fun sendVerificationEmail(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<ApiResponse<Nothing>> {
        authCommandHandler.sendVerifyEmailOtp(userId)
        val apiResponse = ApiResponse<Nothing>(
            status = HttpStatus.OK,
            message = "Send email successful!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Forgot password
    @PostMapping("/forgot-password")
    fun forgotPassword(
        @Valid @RequestBody data: ForgotPasswordDto
    ): ResponseEntity<ApiResponse<LoginResponseDto>> {
        val apiResponse = ApiResponse(
            status = HttpStatus.OK,
            message = "Successfully triggered forgot-password event!",
            data = authCommandHandler.forgotPassword(data.email)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Send Email
    @PostMapping("/send-verification-password")
    fun sendVerificationPassword(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<ApiResponse<Nothing>> {
        authCommandHandler.sendVerifyPasswordOtp(userId)
        val apiResponse = ApiResponse<Nothing>(
            status = HttpStatus.OK,
            message = "Send email successful!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Reset password
    @PostMapping("/reset-password")
    fun resetPassword(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody data: ResetPasswordDto
    ): ResponseEntity<ApiResponse<Nothing>> {
        authCommandHandler.resetPassword(data.otp, data.newPassword, userId)
        val apiResponse = ApiResponse<Nothing>(
            status = HttpStatus.OK,
            message = "Successfully reset password!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Resend 2fa
    @PostMapping("/send-verification-2fa")
    fun sendVerification2FA(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<ApiResponse<Nothing>> {
        authCommandHandler.send2FaOtpEmail(
            userId = userId,
        )
        val apiResponse = ApiResponse<Nothing>(
            status = HttpStatus.OK,
            message = "Send email successful!!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // verify2fa
    @PostMapping("/2fa")
    fun authenticate2FA(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody data: OTPDto,
        @RequestHeader("User-Agent") userAgent: String
    ): ResponseEntity<ApiResponse<LoginResponseDto>> {
        val apiResponse = ApiResponse(
            status = HttpStatus.OK,
            message = "Successfully authenticate 2FA!",
            data = authCommandHandler.twoFactorAuth(
                userId = userId,
                otp = data.otp,
                ua = userAgent
            )
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // refresh token
    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody data: RefreshTokenDto,
        @RequestHeader("User-Agent") userAgent: String
    ): ResponseEntity<ApiResponse<LoginResponseDto>> {
        print("refresh")
        val apiResponse = ApiResponse(
            status = HttpStatus.OK,
            message = "Successfully refreshed!",
            data = authCommandHandler.refreshToken(
                token = data.token,
                ua = userAgent
            )
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Login device
    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody data: LogoutDevicesDto
    ): ResponseEntity<ApiResponse<Nothing>> {
        authCommandHandler.logoutDevices(
            userId = userId,
            data.deviceIds
        )
        val apiResponse = ApiResponse<Nothing>(
            status = HttpStatus.OK,
            message = "Successfully logged out!",
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}