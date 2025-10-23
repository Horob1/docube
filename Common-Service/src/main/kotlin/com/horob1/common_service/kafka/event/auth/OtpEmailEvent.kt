package com.horob1.common_service.kafka.event.auth

import com.horob1.common_service.enums.EmailType

data class OtpEmailEvent(
    val email: String,
    val otp: String,
    val userId: String,
    val emailType: EmailType,
    val name: String
)
