package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.request.CreateUpdateRoleDto
import com.horob1.auth_service.application.command.RoleCommandHandler
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.common_service.api.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1/roles")
class RoleCommandController(
    private val roleCommandHandler: RoleCommandHandler
) {
    @PostMapping
    fun create(
        @Valid @RequestBody data: CreateUpdateRoleDto,
    ): ResponseEntity<ApiResponse<Role>> {
        val apiResponse = ApiResponse.success<Role>(
            status = HttpStatus.CREATED,
            message = "Created role successfully",
            data = roleCommandHandler.create(data)
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse)
    }

    @PutMapping("/{roleId}")
    fun update(
        @PathVariable roleId: UUID,
        @Valid @RequestBody data: CreateUpdateRoleDto,
    ): ResponseEntity<ApiResponse<Role>> {
        val apiResponse = ApiResponse.success<Role>(
            status = HttpStatus.OK,
            message = "Updated role successfully",
            data = roleCommandHandler.update(
                roleId,
                data
            )
        )
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse)
    }

    @DeleteMapping("/{roleId}")
    fun delete(
        @PathVariable roleId: UUID,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val apiResponse = ApiResponse.success<Nothing>(
            status = HttpStatus.OK,
            message = "Updated role successfully",
        )
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse)
    }

    // Patch update user role


}