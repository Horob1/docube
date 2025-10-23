package com.horob1.auth_service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "google")
class GoogleProperties {
    lateinit var clientId: String
}