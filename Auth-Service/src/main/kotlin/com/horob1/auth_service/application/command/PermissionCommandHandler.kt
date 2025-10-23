package com.horob1.auth_service.application.command

import com.horob1.auth_service.api.dto.request.CreateUpdatePermissionDto
import com.horob1.auth_service.domain.repository.PermissionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PermissionCommandHandler(
    private val permissionRepository: PermissionRepository,
) {
    fun create(data: CreateUpdatePermissionDto) = permissionRepository.create(data.name, data.description)

    fun update(id: UUID, data: CreateUpdatePermissionDto) = permissionRepository.update(id, data.name, data.description)

    fun delete(id: UUID) = permissionRepository.deleteById(id)
}