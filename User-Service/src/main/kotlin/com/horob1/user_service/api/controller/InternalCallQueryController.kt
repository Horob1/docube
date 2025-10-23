package com.horob1.user_service.api.controller

import com.horob1.common_service.api.dto.response.UserIdentityDto
import com.horob1.common_service.constant.SecurityConstants.INTERNAL_CALL_HEADER
import com.horob1.user_service.application.query.InternalCallQueryHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user-service")
class InternalCallQueryController(
    private val internalCallQueryHandler: InternalCallQueryHandler,
) {
    @GetMapping("/{email}/email")
    fun getUserIdentityByEmail(
        @PathVariable email: String,
        @RequestHeader(INTERNAL_CALL_HEADER) internalCall: String
    ): ResponseEntity<UserIdentityDto?> {

        return ResponseEntity.status(
            HttpStatus.OK
        ).body(
            internalCallQueryHandler.getUserByEmail(email)
        )
    }

    @GetMapping("/{userId}/user-id")
    fun getUserIdentityById(
        @PathVariable userId: String,
        @RequestHeader(INTERNAL_CALL_HEADER) internalCall: String
    ) : ResponseEntity<UserIdentityDto?> {
        return ResponseEntity.status(
            HttpStatus.OK
        ).body(
            internalCallQueryHandler.getUserById(
                userId,
            )
        )
    }
}