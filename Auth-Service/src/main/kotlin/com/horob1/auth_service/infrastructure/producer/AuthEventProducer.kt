package com.horob1.auth_service.infrastructure.producer

import com.horob1.common_service.kafka.event.auth.OtpEmailEvent
import com.horob1.common_service.kafka.topic.AuthTopic
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AuthEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    fun sendVerifyEmailEvent(event: OtpEmailEvent) {
        kafkaTemplate.send(AuthTopic.getTopic(AuthTopic.OTP_EMAIL), event.userId, event)
    }
}