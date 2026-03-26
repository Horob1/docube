package com.horob1.auth_service

import com.horob1.auth_service.shared.web.config.SharedWebConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@Import(SharedWebConfig::class)
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
