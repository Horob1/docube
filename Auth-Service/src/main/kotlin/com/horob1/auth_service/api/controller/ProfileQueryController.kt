package com.horob1.auth_service.api.controller

import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.service.query.ProfileQueryHandler
import com.horob1.auth_service.shared.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/profile")
class ProfileQueryController(
    private val profileQueryHandler: ProfileQueryHandler
) {
    @GetMapping
    fun getUserProfile(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            message = "Get user summary successfully",
            status = HttpStatus.OK,
            data = profileQueryHandler.getProfile(userId)
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(
            apiResponse
        )
    }
}