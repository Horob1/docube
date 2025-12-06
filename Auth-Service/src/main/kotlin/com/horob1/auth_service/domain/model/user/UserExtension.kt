package com.horob1.auth_service.domain.model.user

fun User.toSummary(): UserSummary = UserSummary(
    id = this.id!!,
    email = this.email,
    firstName = this.firstName,
    lastName = this.lastName,
    phoneNumber = this.phoneNumber,
    address = this.address,
    avatar = this.avatar,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)