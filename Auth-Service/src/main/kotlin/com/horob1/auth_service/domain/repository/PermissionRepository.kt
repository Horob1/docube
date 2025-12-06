package com.horob1.auth_service.domain.repository

import com.horob1.auth_service.domain.model.permission.Permission
import java.util.*

interface PermissionRepository {
    fun findAll(): List<Permission>

    fun findPermissionById(id: UUID): Permission?

    fun save(permission: Permission): Permission

    fun saveAll(permissions: List<Permission>): List<Permission>

    fun findPermissionByName(name: String): Permission?

    fun findPermissionsByIdList(idList: List<UUID>): List<Permission>

    fun existsByName(name: String): Boolean

    fun delete(permission: Permission)

    fun count(): Long
}