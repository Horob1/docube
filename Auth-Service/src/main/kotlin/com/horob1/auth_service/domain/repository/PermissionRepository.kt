package com.horob1.auth_service.domain.repository

import com.horob1.auth_service.domain.model.permission.Permission
import java.util.*

interface PermissionRepository {
    fun create(name: String, description: String): Permission

    fun getPermissionById(id: UUID): Permission

    fun getPermissionList(idList: List<UUID>): List<Permission>

    fun update(id: UUID, newName: String, newDesc: String): Permission

    fun deleteById(id: UUID)

    fun getAll(): List<Permission>
}