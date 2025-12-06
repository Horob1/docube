package com.horob1.auth_service.service.command

import com.horob1.auth_service.api.dto.request.CreateUpdatePermissionDto
import com.horob1.auth_service.api.exception.ExistedPermission
import com.horob1.auth_service.api.exception.NewPermissionNameExist
import com.horob1.auth_service.api.exception.PermissionNotFound
import com.horob1.auth_service.domain.model.permission.Permission
import com.horob1.auth_service.domain.repository.PermissionRepository
import com.horob1.common_service.api.exception.AppException
import org.springframework.stereotype.Service
import java.util.*

@Service
class PermissionCommandHandler(
    private val permissionRepository: PermissionRepository,
) {
    fun create(data: CreateUpdatePermissionDto): Permission {
        if (permissionRepository.existsByName(data.name)) throw AppException(ExistedPermission)
        return permissionRepository.save(Permission(data.name, data.description))
    }

    fun update(id: UUID, data: CreateUpdatePermissionDto): Permission {
        val permission = permissionRepository.findPermissionById(id) ?: throw AppException(PermissionNotFound)
        if (permission.name != data.name && permissionRepository.existsByName(data.name)) throw AppException(
            NewPermissionNameExist
        )
        permission.name = data.name
        permission.description = data.description
        return permissionRepository.save(permission)
    }

    fun delete(id: UUID) {
        val permission = permissionRepository.findPermissionById(id) ?: throw AppException(PermissionNotFound)
        permissionRepository.delete(permission)
    }
}