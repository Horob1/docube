package com.horob1.auth_service.api.controller

import com.horob1.auth_service.service.query.PermissionQueryHandler
import com.horob1.auth_service.domain.model.permission.Permission
import com.horob1.common_service.api.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/permissions")
class PermissionQueryController(
    private val permissionQueryHandler: PermissionQueryHandler
) {

    @GetMapping("/all")
    fun getAll(): ResponseEntity<ApiResponse<List<Permission>>> {
        val apiResponse = ApiResponse.success(
            status = HttpStatus.OK,
            data = permissionQueryHandler.getAll(),
            message = "Get all permissions successful"
        )
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse)
    }

    @PreAuthorize("hasAuthority('permission:view') or hasAuthority('permission:full_access')")
    @GetMapping("/{permissionID}")
    fun getByID(@PathVariable permissionID: UUID): ResponseEntity<ApiResponse<Permission>> {
        val apiResponse = ApiResponse.success(
            status = HttpStatus.OK,
            data = permissionQueryHandler.getByID(permissionID),
            message = "Permissions found"
        )
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse)
    }
}