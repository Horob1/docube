package com.horob1.auth_service.service.command

import com.horob1.auth_service.api.dto.request.CreateUpdateRoleDto
import com.horob1.auth_service.api.exception.ExistedRole
import com.horob1.auth_service.api.exception.NewPermissionNameExist
import com.horob1.auth_service.api.exception.NewRoleNameExist
import com.horob1.auth_service.api.exception.RoleNotFound
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.repository.PermissionRepository
import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.auth_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleCommandHandler(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) {
    fun create(
        data: CreateUpdateRoleDto,
    ): Role {
        if (roleRepository.existsByName(data.name)) throw AppException(ExistedRole)
        val permissionList = permissionRepository.findPermissionsByIdList(
            data.permissions
        )
        return roleRepository.save(Role(data.name, data.description, permissionList.toMutableSet()))
    }

    fun update(
        id: UUID,
        data: CreateUpdateRoleDto,
    ): Role {
        val role = roleRepository.findRoleById(id) ?: throw AppException(RoleNotFound)
        if (role.name != data.name && roleRepository.existsByName(data.name)) throw AppException(
            NewRoleNameExist
        )
        val permissionList = permissionRepository.findPermissionsByIdList(
            data.permissions
        )
        role.name = data.name
        role.description = data.description
        role.permissions = permissionList.toMutableSet()
        return roleRepository.save(role)
    }

    fun delete(id: UUID) {
        val role = roleRepository.findRoleById(id) ?: throw AppException(RoleNotFound)
        roleRepository.delete(role)
    }
}