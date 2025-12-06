package com.horob1.auth_service.api.controller

import com.horob1.auth_service.service.query.AuthQueryHandler
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.model.token.Token
import com.horob1.common_service.api.dto.response.ApiResponse
import com.horob1.common_service.constant.SecurityConstants.USER_ID_HEADER
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auth")
class AuthQueryController(
    private val authQueryHandler: AuthQueryHandler
) {
    // List my devices
    @GetMapping("/devices")
    fun getDevices(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<ApiResponse<List<Token>>> {
        val apiResponse = ApiResponse.success(
            status = HttpStatus.OK,
            message = "Get all devices for user $userId",
            data = authQueryHandler.getMyDevices(userId)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    // Get my role
    @GetMapping("/roles")
    fun getRoles(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<ApiResponse<List<Role>>> {
        val apiResponse = ApiResponse.success(
            status = HttpStatus.OK,
            message = "Get all roles for user $userId",
            data = authQueryHandler.getMyRoles(userId)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}