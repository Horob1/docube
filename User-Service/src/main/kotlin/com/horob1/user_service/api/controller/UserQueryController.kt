package com.horob1.user_service.api.controller

import com.horob1.common_service.api.dto.response.ApiResponse
import com.horob1.user_service.application.query.UserQueryHandler
import com.horob1.user_service.domain.user.UserSummary
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserQueryController(
    private val userQueryHandler: UserQueryHandler
) {
    @GetMapping("/{userId}")
    fun createUser(
        @PathVariable userId: String,
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            message = "Create user successfully",
            status = HttpStatus.OK,
            data = userQueryHandler.getUserSummary(userId)
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(
            apiResponse
        )
    }
}