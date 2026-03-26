package com.horob1.auth_service.shared.kafka.topic

object AuthTopic {
    const val PREFIX = "auth"
    const val OTP_EMAIL = "otp-email"
    const val VERIFY_EMAIL_SUCCESSFULLY = "verify-email_successfully"
    const val RESET_PASSWORD = "reset-password"

    fun getTopic(
        topic: String,
    ) = "$PREFIX.$topic"
}