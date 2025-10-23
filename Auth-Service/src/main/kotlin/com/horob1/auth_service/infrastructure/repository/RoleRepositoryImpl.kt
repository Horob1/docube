package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.api.exception.ExistedRole
import com.horob1.auth_service.api.exception.PermissionNotFound
import com.horob1.auth_service.api.exception.RoleNotFound
import com.horob1.auth_service.domain.model.permission.Permission
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.common_service.api.exception.AppException
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RoleRepositoryImpl(
    private val rolePostgresRepository: RolePostgresRepository,
    private val permissionPostgresRepository: PermissionPostgresRepository,
    private val urmPostgresRepository: URMPostgresRepository
) : RoleRepository {
    override fun create(
        name: String,
        description: String,
        permissionList: List<UUID>
    ): Role {
        if (rolePostgresRepository.existsByName(name)) {
            throw AppException(ExistedRole)
        }

        return rolePostgresRepository.save(
            Role(
                name = name,
                description = description,
                permissions = getAndCheckPermissions(permissionList).toMutableSet()
            )
        )
    }

    override fun getRoleById(id: UUID): Role {
        return rolePostgresRepository.findById(id).orElseThrow {
            throw AppException(RoleNotFound)
        }
    }

    override fun getRoleList(idList: List<UUID>): List<Role> {
        return rolePostgresRepository.findAllById(idList)
    }

    override fun update(
        id: UUID,
        newName: String,
        newDesc: String,
        newPermissionList: List<UUID>
    ): Role {
        val role = getRoleById(id)
        if (role.name != newName) {
            if (rolePostgresRepository.existsByName(newName)) {
                throw AppException(ExistedRole)
            }
        }
        role.description = newDesc
        role.name = newName
        role.permissions = getAndCheckPermissions(
            newPermissionList
        ).toMutableSet()

        return rolePostgresRepository.save(
            role
        )
    }


    override fun deleteById(id: UUID) {
        // TODO: cấm xoá các role mặc định, check user có đúng role đấy gán lại role mặc định
        rolePostgresRepository.deleteById(id)
    }

    override fun getAll(): List<Role> {
        return rolePostgresRepository.findAll()
    }

    override fun getRolesByUserId(userId: String): List<Role> {
        val mappings = urmPostgresRepository.getAllByUserId(
            userId
        )
        val roles = mappings.map {
            it.role
        }

        return roles
    }

    fun getAndCheckPermissions(
        permissionList: List<UUID>,
    ): List<Permission> {
        val permits = permissionPostgresRepository.findAllById(
            permissionList
        )

        if (permits.size != permissionList.size) {
            throw AppException(
                PermissionNotFound
            )
        }

        return permits
    }

}