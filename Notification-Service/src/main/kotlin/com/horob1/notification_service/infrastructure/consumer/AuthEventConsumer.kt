package com.horob1.notification_service.infrastructure.consumer

import com.horob1.common_service.enums.EmailType
import com.horob1.common_service.kafka.event.auth.OtpEmailEvent
import com.horob1.common_service.kafka.topic.AuthTopic
import com.horob1.notification_service.infrastructure.service.EmailService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class AuthEventConsumer(
    private val emailService: EmailService
) {
    @KafkaListener(topics = ["${AuthTopic.PREFIX}.${AuthTopic.OTP_EMAIL}"])
    fun handleOtpEmailEvent(event: OtpEmailEvent) {
        val title = when (event.emailType) {
            EmailType.VERIFICATION -> "Verification email otp"
            EmailType.TWO_FACTOR_AUTH -> "Two-Factor auth email otp"
            EmailType.RESET_PASSWORD -> "Reset password email otp"
        }
        emailService.sendOtpEmail(
            email = event.email,
            otp = event.otp,
            recipientName = event.name,
            title = title,
        )
    }
}