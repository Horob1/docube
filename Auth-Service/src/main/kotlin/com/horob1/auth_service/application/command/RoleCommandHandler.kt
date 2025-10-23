package com.horob1.auth_service.application.command

import com.horob1.auth_service.api.dto.request.CreateUpdateRoleDto
import com.horob1.auth_service.domain.repository.RoleRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleCommandHandler(
    private val roleRepository: RoleRepository
) {
    fun create(
        data: CreateUpdateRoleDto,
    ) = roleRepository.create(
        data.name,
        data.description,
        data.permissions
    )

    fun update(
        id: UUID,
        data: CreateUpdateRoleDto,
    ) = roleRepository.update(
        id,
        data.name,
        data.description,
        data.permissions
    )

    fun delete(id: UUID) = roleRepository.deleteById(id)
}