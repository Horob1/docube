package com.horob1.gateway.api.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import com.horob1.gateway.api.response.ApiResponse
import com.horob1.gateway.enum.TokenType
import com.horob1.gateway.util.jwt.JwtTokenManager
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class VerifyUserPermissionFilter(
    private val jwtTokenManager: JwtTokenManager,
    private val objectMapper: ObjectMapper,
    private val webClient: WebClient,
) : GatewayFilter {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val response = exchange.response

        val authHeader = request.headers.getFirst("Authorization")

        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            return sendError(
                response,
                HttpStatus.UNAUTHORIZED,
                "Missing or invalid Authorization header",
                "GATEWAY_0000"
            )
        }

        val token = authHeader.removePrefix("Bearer ").trim()

        return try {
            val payload = jwtTokenManager.validateAndParse(token, TokenType.ACCESS)
            val userId = payload.subject

            webClient.get()
                .uri("lb://auth-service/api/v1/auth-service/$userId/permissions")
                .header("X-Internal-Call", "true")
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<String>>() {})
                .flatMap { permissions ->
                    val permissionsJson = objectMapper.writeValueAsString(permissions)

                    val mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Permissions", permissionsJson)
                        .build()

                    val mutatedExchange = exchange.mutate().request(mutatedRequest).build()
                    chain.filter(mutatedExchange)
                }
                .onErrorResume {
                    val permissionsJson = objectMapper.writeValueAsString(emptyList<String>())
                    val mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Permissions", permissionsJson)
                        .build()

                    val mutatedExchange = exchange.mutate().request(mutatedRequest).build()
                    chain.filter(mutatedExchange)
                }
        } catch (e: Exception) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token", "GATEWAY_0001")
        }
    }

    private fun sendError(
        response: org.springframework.http.server.reactive.ServerHttpResponse,
        status: HttpStatusCode,
        message: String,
        code: String
    ): Mono<Void> {
        response.statusCode = status
        response.headers.contentType = org.springframework.http.MediaType.APPLICATION_JSON

        val body = ApiResponse.error<Unit>(
            status = status,
            message = message,
            code = code
        )

        val buffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(body))
        return response.writeWith(Mono.just(buffer))
    }
}
