package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.request.CreateUserDto
import com.horob1.auth_service.api.dto.request.UpdateRoleDto
import com.horob1.auth_service.api.dto.request.UpdateUserDto
import com.horob1.auth_service.domain.model.user.UserSummary
import com.horob1.auth_service.service.command.UserCommandHandler
import com.horob1.common_service.api.dto.response.ApiResponse

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
class UserCommandController(
    private val userCommandHandler: UserCommandHandler,
) {
    @PostMapping
    fun createUser(
        @Valid @RequestBody user: CreateUserDto
    ): ResponseEntity<ApiResponse<UserSummary>> {
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
            data = userCommandHandler.update(UUID.fromString(userId), user)
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(apiResponse)
    }

    @DeleteMapping("/{userId}/ban")
    fun banUser(
        @PathVariable userId: String,
    ): ResponseEntity<ApiResponse<Nothing>> {
        userCommandHandler.banUser(UUID.fromString(userId))
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
        userCommandHandler.unbanUser(UUID.fromString(userId))
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Unban user successfully",
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(apiResponse)
    }

    @PatchMapping("/{userId}/update-role")
    fun undateRoleUser(
        @PathVariable userId: String,
        @Valid @RequestBody data: UpdateRoleDto
    ): ResponseEntity<ApiResponse<Nothing>> {
        userCommandHandler.updateRole(UUID.fromString(userId), data.roles)
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Update user successfully",
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(
            apiResponse.status
        ).body(apiResponse)
    }
}