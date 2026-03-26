package com.horob1.auth_service.api.controller

import com.horob1.auth_service.service.query.InternalCallQueryHandler
import com.horob1.auth_service.shared.exception.AppError
import com.horob1.auth_service.shared.exception.AppException
import com.horob1.auth_service.shared.constant.SecurityConstants.INTERNAL_CALL_HEADER
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auth-service")
class InternalCallQueryController(
    private val internalCallQueryHandler: InternalCallQueryHandler
) {
    @GetMapping(
        "/{userId}/permissions"
    )
    fun getPermissionByUserId(
        @PathVariable userId: UUID,
        @RequestHeader(INTERNAL_CALL_HEADER) internalCall: String
    ): ResponseEntity<List<String>> {
        if (internalCall != "true") throw AppException(AppError.NotInternalCall)
        return ResponseEntity.ok(internalCallQueryHandler.getPermissionsByUserId(userId))
    }
}