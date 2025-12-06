package com.horob1.auth_service.service.query

import com.horob1.auth_service.api.exception.RoleNotFound
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.common_service.api.exception.AppException
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleQueryHandler(
    private val roleRepository: RoleRepository
) {
    fun findAll(): List<Role> {
        return roleRepository.findAll()
    }

    fun getById(id: UUID): Role {
        return roleRepository.findRoleById(id) ?: throw AppException(RoleNotFound)
    }

}