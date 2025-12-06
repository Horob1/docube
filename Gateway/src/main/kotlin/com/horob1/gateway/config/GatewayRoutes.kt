package com.horob1.gateway.config

import com.horob1.common_service.enums.TokenType
import com.horob1.gateway.api.interceptor.VerifyUserFilterFactory
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
                    .or(path("/api/v1/auth/send-verification-email"))
                filters {
                    filter(
                        verifyUserFilterFactory.apply(
                            VerifyUserFilterFactory.Config(tokenType = TokenType.EMAIL_VERIFY)
                        )
                    )
                }
                uri("lb://AUTH-SERVICE")
            }

            route("auth-reset-password") {
                path("/api/v1/auth/send-verification-password")
                    .or(path("/api/v1/auth/reset-password"))
                filters {
                    filter(
                        verifyUserFilterFactory.apply(
                            VerifyUserFilterFactory.Config(tokenType = TokenType.PASSWORD_RESET)
                        )
                    )
                }
                uri("lb://AUTH-SERVICE")
            }

            route("auth-2fa") {
                path("/api/v1/auth/send-verification-2fa")
                    .or(path("/api/v1/auth/2fa"))
                filters {
                    filter(
                        verifyUserFilterFactory.apply(
                            VerifyUserFilterFactory.Config(tokenType = TokenType.PASSWORD_RESET)
                        )
                    )
                }
                uri("lb://AUTH-SERVICE")
            }

            route("auth-logout") {
                path("/api/v1/auth/logout")
                filters {
                    filter(
                        verifyUserFilterFactory.apply(
                            VerifyUserFilterFactory.Config(tokenType = TokenType.ACCESS)
                        )
                    )
                }
                uri("lb://AUTH-SERVICE")
            }

            route("auth") {
                path("/api/v1/auth/**")
                uri("lb://AUTH-SERVICE")
            }
        }
    }
}
