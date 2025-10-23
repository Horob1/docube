package com.horob1.common_service.kafka.topic

object AuthTopic {
    const val PREFIX = "auth"
    const val OTP_EMAIL = "otp-email"
    const val VERIFY_EMAIL_SUCCESSFULLY = "verify-email_successfully"

    fun getTopic(
        topic: String,
    ) = "$PREFIX.$topic"
}