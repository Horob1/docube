package com.horob1.auth_service.config

import com.horob1.common_service.constant.SecurityConstants
import com.horob1.web_core.interceptor.UserPermissionContextFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userContextFilter: UserPermissionContextFilter
) {
    companion object {
        private val PUBLIC_ENDPOINTS = arrayOf(
            "/api/v1/auth/login",
            "/api/v1/auth/google",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth-service/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/health",
            "/actuator/**",
        )

        private val PROTECTED_ENDPOINTS = arrayOf(
            "/api/v1/roles/**",
            "/api/v1/permissions/**",
            "/api/v1/auth/verify-email",
            "/api/v1/auth/send-verification-email",
            "/api/v1/auth/send-verification-password",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/send-verification-2fa",
            "/api/v1/auth/2fa",
            "/api/v1/auth/logout",
        )
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }

            authorizeHttpRequests {
                PUBLIC_ENDPOINTS.forEach { authorize(it, permitAll) }

                PROTECTED_ENDPOINTS.forEach {
                    authorize(it, authenticated)
                }

                authorize(anyRequest, denyAll)
            }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            anonymous { disable() }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(userContextFilter)
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(SecurityConstants.BCRYPT_STRENGTH)
    }
}