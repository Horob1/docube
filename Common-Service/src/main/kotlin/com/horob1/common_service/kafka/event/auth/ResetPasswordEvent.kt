package com.horob1.common_service.kafka.event.auth

data class ResetPasswordEvent(
    val userId: String,
    val password: String,
)