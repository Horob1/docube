package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.request.CreateUpdateRoleDto
import com.horob1.auth_service.service.command.RoleCommandHandler
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.shared.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1/roles")
class RoleCommandController(
    private val roleCommandHandler: RoleCommandHandler
) {
    @PreAuthorize("hasAuthority('role:add') or hasAuthority('role:full_access')")
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

    @PreAuthorize("hasAuthority('role:edit') or hasAuthority('role:full_access')")
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

    @PreAuthorize("hasAuthority('role:delete') or hasAuthority('role:full_access')")
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
}