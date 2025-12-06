package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.request.ChangePasswordDto
import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.service.command.ProfileCommandHandler
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
@RequestMapping("/api/v1/profile")
class ProfileCommandController(
    private val profileCommandHandler: ProfileCommandHandler,
) {
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

    @PostMapping("/2fa")
    fun toggle2FA(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            HttpStatus.OK,
            message = "Toggle 2fa successfully",
            data = profileCommandHandler.toggle2FA(
                userId,
            )
        )
        return ResponseEntity(apiResponse, HttpStatus.OK)
    }

}