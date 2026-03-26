package com.horob1.auth_service.api.controller

import com.horob1.auth_service.api.dto.response.AuthorProfileResponse
import com.horob1.auth_service.service.query.AuthorQueryHandler
import com.horob1.auth_service.shared.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/authors")
class AuthorQueryController(
    private val authorQueryHandler: AuthorQueryHandler
) {
    /**
     * GET /api/v1/authors/{userId}
     *
     * Public endpoint — no authentication required.
     * Returns minimal public profile of a document author.
     * Intentionally omits sensitive fields: email, phone, address, roles, status.
     */
    @GetMapping("/{userId}")
    fun getAuthorProfile(
        @PathVariable userId: UUID,
    ): ResponseEntity<ApiResponse<AuthorProfileResponse>> {
        val apiResponse = ApiResponse.success(
            message = "Author profile retrieved successfully",
            status = HttpStatus.OK,
            data = authorQueryHandler.getAuthorProfile(userId)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
