package com.horob1.auth_service.application.query

import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.repository.RoleRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleQueryHandler(
    private val roleRepository: RoleRepository
) {
    fun findAll(): List<Role> = roleRepository.getAll()

    fun getById(id: UUID) = roleRepository.getRoleById(id)

    fun getRolesByUserId(userId: String) = roleRepository.getRolesByUserId(userId)
}