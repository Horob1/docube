package com.horob1.user_service.infrastructure.consumer

import com.horob1.common_service.enums.UserStatus
import com.horob1.common_service.kafka.event.auth.UserIdEvent
import com.horob1.common_service.kafka.topic.AuthTopic
import com.horob1.user_service.domain.user.UserRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class AuthEventConsumer(
    private val userRepository: UserRepository
) {
    @KafkaListener(topics = ["${AuthTopic.PREFIX}.${AuthTopic.VERIFY_EMAIL_SUCCESSFULLY}"])
    fun handleOtpEmailEvent(event: UserIdEvent) {
        val userId = event.userId
        val user = userRepository.findById(userId)
        if (user != null) {
            userRepository.save(
                user.copy(
                    status = UserStatus.ACTIVE,
                )
            )
        }
    }
}