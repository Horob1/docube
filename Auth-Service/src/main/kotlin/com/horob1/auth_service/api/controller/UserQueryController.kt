package com.horob1.auth_service.api.controller

import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.service.query.UserQueryHandler
import com.horob1.auth_service.shared.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
class UserQueryController(
    private val userQueryHandler: UserQueryHandler
) {
    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: String,
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            message = "Get user summary successfully",
            status = HttpStatus.OK,
            data = userQueryHandler.getUserSummary(UUID.fromString(userId))
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(
            apiResponse
        )
    }
}