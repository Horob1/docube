package com.horob1.user_service.api.controller

import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.common_service.api.dto.response.ApiResponse
import com.horob1.user_service.api.dto.request.UpdateUserDto
import com.horob1.user_service.application.command.UserCommandHandler
import com.horob1.user_service.domain.user.UserSummary
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserCommandController(
    private val userCommandHandler: UserCommandHandler,
) {
    @PostMapping
    fun createUser(
        @Valid @RequestBody user: CreateUserDto
    ): ResponseEntity<

            ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            message = "Create user successfully",
            status = HttpStatus.CREATED,
            data = userCommandHandler.create(user)
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(
            apiResponse
        )
    }

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: String,
        @Valid @RequestBody user: UpdateUserDto
    ): ResponseEntity<ApiResponse<UserSummary>> {
        val apiResponse = ApiResponse.success(
            message = "Update user successfully",
            status = HttpStatus.OK,
            data = userCommandHandler.update(userId, user)
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(apiResponse)
    }

    @DeleteMapping("/{userId}/ban")
    fun banUser(
        @PathVariable userId: String,
    ): ResponseEntity<ApiResponse<Nothing>> {
        userCommandHandler.banUser(userId)
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Ban user successfully",
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(apiResponse)
    }

    @PatchMapping("/{userId}/unban")
    fun unbanUser(
        @PathVariable userId: String,
    ): ResponseEntity<ApiResponse<Nothing>> {
        userCommandHandler.unbanUser(userId)
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Unban user successfully",
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(apiResponse)
    }
}