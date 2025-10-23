package com.horob1.auth_service.config

import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            template.header("X-Internal-Call", "true")
        }
    }


}