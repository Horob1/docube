package com.horob1.auth_service.infrastructure.client

import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.common_service.api.dto.response.UserIdentityDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "User-Service")
interface UserServiceClient {
    @GetMapping("/api/v1/user-service/{email}/email")
    fun getUserIdentityByEmail(@PathVariable email: String): UserIdentityDto?

    @GetMapping("/api/v1/user-service/{userId}/user-id")
    fun getUserIdentityById(@PathVariable userId: String): UserIdentityDto?

    @PostMapping("/api/v1/user-service/create")
    fun createUser(@RequestBody body: CreateUserDto): String
}
