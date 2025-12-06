package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.domain.model.permission.Permission
import com.horob1.auth_service.domain.repository.PermissionRepository
import com.horob1.auth_service.infrastructure.repository.postgres.PermissionPostgresRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PermissionRepositoryImpl(
    private val permissionPostgresRepository: PermissionPostgresRepository
) : PermissionRepository {
    override fun findAll(): List<Permission> {
        return permissionPostgresRepository.findAll()
    }

    override fun findPermissionById(id: UUID): Permission? {
        return permissionPostgresRepository.findById(id).orElse(null)
    }

    override fun save(permission: Permission): Permission {
        return permissionPostgresRepository.save(permission)
    }

    override fun saveAll(permissions: List<Permission>): List<Permission> {
        return permissionPostgresRepository.saveAll(permissions)
    }

    override fun findPermissionByName(name: String): Permission? {
        return permissionPostgresRepository.findByName(name)
    }

    override fun findPermissionsByIdList(idList: List<UUID>): List<Permission> {
        return permissionPostgresRepository.findAllById(idList)
    }

    override fun existsByName(name: String): Boolean {
        return permissionPostgresRepository.existsByName(name)
    }

    override fun delete(permission: Permission) {
        permissionPostgresRepository.delete(permission)
    }

    override fun count(): Long {
        return permissionPostgresRepository.count()
    }


}