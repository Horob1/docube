package com.horob1.auth_service.shared.kafka.event

import com.horob1.auth_service.shared.enums.EmailType

data class OtpEmailEvent(
    val email: String,
    val otp: String,
    val userId: String,
    val emailType: EmailType,
    val name: String
)
