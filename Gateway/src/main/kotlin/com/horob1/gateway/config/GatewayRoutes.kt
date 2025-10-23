package com.horob1.gateway.config

import com.horob1.gateway.api.interceptor.VerifyUserFilterFactory
import com.horob1.gateway.enum.TokenType
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRoutes(
    private val verifyUserFilterFactory: VerifyUserFilterFactory
) {

    @Bean
    fun customRoutes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes {
            // Route cho auth verify email
            route("auth-verify-email") {
                path("/api/v1/auth/verify-email")
                filters {
                    filter(
                        verifyUserFilterFactory.apply(
                            VerifyUserFilterFactory.Config(tokenType = TokenType.EMAIL_VERIFY)
                        )
                    )
                }
                uri("lb://AUTH-SERVICE")
            }

            route("auth-send-verification-email") {
                path("/api/v1/auth/send-verification-email")
                filters {
                    filter(
                        verifyUserFilterFactory.apply(
                            VerifyUserFilterFactory.Config(tokenType = TokenType.EMAIL_VERIFY)
                        )
                    )
                }
                uri("lb://AUTH-SERVICE")
            }
            // Route cho /profile/**
            route("user-service-profile") {
                path("/api/v1/profile/**")
                filters {
                }
                uri("lb://USER-SERVICE")
            }
        }
    }
}
