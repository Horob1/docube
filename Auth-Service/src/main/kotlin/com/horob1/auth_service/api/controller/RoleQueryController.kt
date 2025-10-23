package com.horob1.auth_service.api.controller

import com.horob1.auth_service.application.query.RoleQueryHandler
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.common_service.api.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("api/v1/roles")
class RoleQueryController(
    private val roleQueryHandler: RoleQueryHandler
) {
    @GetMapping("/all")
    fun getAll(): ResponseEntity<ApiResponse<List<Role>>> {
        val apiResponse = ApiResponse.success(
            status = HttpStatus.OK,
            message = "All Roles Found",
            data = roleQueryHandler.findAll()
        )
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse)
    }

    @GetMapping("/{roleId}")
    fun getById(@PathVariable("roleId") roleId: UUID): ResponseEntity<ApiResponse<Role>> {
        val apiResponse = ApiResponse.success(
            status = HttpStatus.OK,
            message = "Role Found",
            data = roleQueryHandler.getById(roleId)
        )
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse)
    }
}