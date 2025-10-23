package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.api.exception.ExistedPermission
import com.horob1.auth_service.api.exception.NewPermissionNameExist
import com.horob1.auth_service.api.exception.PermissionNotFound
import com.horob1.auth_service.domain.model.permission.Permission
import com.horob1.auth_service.domain.repository.PermissionRepository
import com.horob1.common_service.api.exception.AppException
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PermissionRepositoryImpl(
    private val permissionPostgresRepository: PermissionPostgresRepository
) : PermissionRepository {
    override fun create(
        name: String,
        description: String
    ): Permission {
        val existedPermission = permissionPostgresRepository.existsByName(name)
        if (existedPermission) {
            throw AppException(ExistedPermission)
        }
        return permissionPostgresRepository.save(
            Permission(
                name = name,
                description = description,
            )
        )
    }

    override fun getPermissionById(id: UUID): Permission {
        return permissionPostgresRepository.findById(id).orElseThrow {
            throw AppException(PermissionNotFound)
        }
    }

    override fun getPermissionList(idList: List<UUID>): List<Permission> {
        return permissionPostgresRepository.findAllById(idList)
    }

    override fun update(
        id: UUID,
        newName: String,
        newDesc: String
    ): Permission {
        val permission = permissionPostgresRepository.findById(id).orElseThrow {
            throw AppException(PermissionNotFound)
        }
        if (newName != permission.name) {
            if (permissionPostgresRepository.existsByName(newName)) {
                throw AppException(NewPermissionNameExist)
            }
        }

        permission.name = newName
        permission.description = newDesc

        return permissionPostgresRepository.save(
            permission
        )
    }

    override fun deleteById(id: UUID) {
        return permissionPostgresRepository.deleteById(id)
    }

    override fun getAll(): List<Permission> {
        return permissionPostgresRepository.findAll()
    }

}