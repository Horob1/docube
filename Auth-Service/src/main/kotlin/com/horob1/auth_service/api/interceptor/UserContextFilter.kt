package com.horob1.auth_service.api.interceptor

import com.horob1.common_service.constant.SecurityConstants.USER_ID_HEADER
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class UserContextFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val userId = request.getHeader(USER_ID_HEADER)

        if (userId != null && userId.isNotBlank()) {

            val authentication = UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.emptyList()
            )

            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}