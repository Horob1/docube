package com.horob1.auth_service.service.query

import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.auth_service.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class InternalCallQueryHandler(
    private val userRepository: UserRepository,
) {
    fun getPermissionsByUserId(userId: UUID): List<String> {
        val user = userRepository.findById(id = userId) ?: return emptyList()
        val permissionSet = mutableSetOf<String>()
        for (role in user.roles) {
            permissionSet.addAll(
                role.permissions.map { it.name }
            )
        }
        return permissionSet.toList()
    }
}