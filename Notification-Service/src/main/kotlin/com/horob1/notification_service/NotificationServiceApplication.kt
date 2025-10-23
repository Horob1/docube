package com.horob1.notification_service

import com.horob1.common_service.CommonConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@EnableDiscoveryClient
@SpringBootApplication
@Import(CommonConfig::class)
class NotificationServiceApplication

fun main(args: Array<String>) {
	runApplication<NotificationServiceApplication>(*args)
}
