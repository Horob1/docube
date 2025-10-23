package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.request.GoogleAuthDto
import com.horob1.auth_service.api.dto.request.LoginDto
import com.horob1.auth_service.api.dto.request.OTPDto
import com.horob1.auth_service.api.dto.request.RefreshTokenDto
import com.horob1.auth_service.api.dto.request.RegisterDto
import com.horob1.auth_service.api.dto.response.LoginResponseDto
import com.horob1.auth_service.application.command.AuthCommandHandler
import com.horob1.common_service.api.dto.response.ApiResponse
import com.horob1.common_service.constant.SecurityConstants.USER_ID_HEADER
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
//TODO: trả token theo client
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
        val apiResponse = ApiResponse<LoginResponseDto>(
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
        val apiResponse = ApiResponse<LoginResponseDto>(
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
        @RequestHeader(USER_ID_HEADER) userId: String,
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
        @RequestHeader(USER_ID_HEADER) userId: String,
    ): ResponseEntity<ApiResponse<Nothing>> {
        authCommandHandler.sendVerifyEmailOtp(userId)
        val apiResponse = ApiResponse<Nothing>(
            status = HttpStatus.OK,
            message = "Send email successful!"
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Forgot password

    // Send Email

    // Reset password

    // refresh token
    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody data: RefreshTokenDto,
        @RequestHeader("User-Agent") userAgent: String
    ) : ResponseEntity<ApiResponse<LoginResponseDto>> {
        print("refresh")
        val apiResponse = ApiResponse<LoginResponseDto>(
            status = HttpStatus.OK,
            message = "Successfully refreshed!",
            data = authCommandHandler.refreshToken(
                token = data.token,
                ua = userAgent
            )
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Logout devices

    // Login current device
}