package com.horob1.common_service.api.interceptor

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.horob1.common_service.constant.SecurityConstants.USER_ID_HEADER
import com.horob1.common_service.constant.SecurityConstants.USER_PERMISSIONS_HEADER
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class UserPermissionContextFilter : OncePerRequestFilter() {

    private val mapper = ObjectMapper()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val userId = request.getHeader(USER_ID_HEADER)

        if (userId != null && userId.isNotBlank()) {
            val permissionsHeader = request.getHeader(USER_PERMISSIONS_HEADER)
            val authorities = parsePermissions(permissionsHeader)

            // 3. Tạo đối tượng Authentication với đầy đủ quyền
            val authentication = UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
            )

            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun parsePermissions(permissionsJson: String?): List<GrantedAuthority> {
        if (permissionsJson.isNullOrBlank()) {
            return emptyList()
        }

        return try {
            val permissionStrings: List<String> =
                mapper.readValue(permissionsJson, object : TypeReference<List<String>>() {})
            permissionStrings.map { SimpleGrantedAuthority(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}