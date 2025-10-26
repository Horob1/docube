package com.horob1.auth_service.application.query

import com.horob1.auth_service.domain.repository.RoleRepository
import org.springframework.stereotype.Service

@Service
class InternalCallQueryHandler(
    private val roleRepository: RoleRepository,
) {
    fun getPermissionsByUserId(userId: String): List<String> {
        val roles = roleRepository.getRolesByUserId(userId)
        val permissionSet = mutableSetOf<String>()
        for (role in roles) {
            permissionSet.addAll(
                role.permissions.map { it.name }
            )
        }
        return permissionSet.toList()
    }
}