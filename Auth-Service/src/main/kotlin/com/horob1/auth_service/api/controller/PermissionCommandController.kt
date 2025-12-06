package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.request.CreateUpdatePermissionDto
import com.horob1.auth_service.service.command.PermissionCommandHandler
import com.horob1.auth_service.domain.model.permission.Permission
import com.horob1.common_service.api.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1/permissions")
class PermissionCommandController(
    private val permissionCommandHandler: PermissionCommandHandler
) {
    @PreAuthorize("hasAuthority('permission:add') or hasAuthority('permission:full_access')")
    @PostMapping
    fun create(
        @Valid @RequestBody data: CreateUpdatePermissionDto
    ): ResponseEntity<ApiResponse<Permission>> {
        val apiResponse = ApiResponse.success(
            status = HttpStatus.CREATED,
            data = permissionCommandHandler.create(data),
            message = "Permission created"
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse)
    }

    @PreAuthorize("hasAuthority('permission:edit') or hasAuthority('permission:full_access')")
    @PutMapping("/{permissionId}")
    fun update(
        @Valid @RequestBody data: CreateUpdatePermissionDto,
        @PathVariable permissionId: UUID
    ): ResponseEntity<ApiResponse<Permission>> {
        val apiResponse = ApiResponse.success(
            status = HttpStatus.OK,
            message = "Permission updated",
            data = permissionCommandHandler.update(
                permissionId,
                data
            )
        )
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse)
    }

    @PreAuthorize("hasAuthority('permission:delete')  or hasAuthority('permission:full_access')")
    @DeleteMapping("/{permissionId}")
    fun delete(
        @PathVariable permissionId: UUID
    ): ResponseEntity<ApiResponse<Nothing>> {
        permissionCommandHandler.delete(permissionId)
        val apiResponse = ApiResponse.success<Nothing>(
            status = HttpStatus.OK,
            message = "Permission deleted",
        )
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse)
    }
}