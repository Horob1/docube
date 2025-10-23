package com.horob1.user_service.api.controller

import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.common_service.constant.SecurityConstants.INTERNAL_CALL_HEADER
import com.horob1.user_service.application.command.InternalCallCommandHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user-service")
class InternalCallCommandController(
    private val internalCallCommandHandler: InternalCallCommandHandler
) {
    @PostMapping("/create")
    fun createUser(@RequestBody user: CreateUserDto, @RequestHeader(INTERNAL_CALL_HEADER) internalCall: String): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.OK).body(internalCallCommandHandler.registerUser(user))
    }
}