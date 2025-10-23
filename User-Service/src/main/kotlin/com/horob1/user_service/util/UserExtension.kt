package com.horob1.user_service.util

import com.horob1.common_service.api.dto.request.CreateUserDto
import com.horob1.user_service.domain.user.User

fun CreateUserDto.toUser() = User(
    email = this.email,
    firstName = this.firstName,
    lastName = this.lastName,
    phoneNumber = this.phoneNumber,
    password = this.password,
    address = this.address,
    status = this.status,
)