package com.horob1.auth_service.domain.repository

import com.horob1.auth_service.domain.model.role.Role
import java.util.*

interface RoleRepository {
    fun create(name: String, description: String, permissionList: List<UUID>): Role

    fun getRoleById(id: UUID): Role

    fun getRoleList(idList: List<UUID>): List<Role>

    fun update(id: UUID, newName: String, newDesc: String, newPermissionList: List<UUID>): Role

    fun deleteById(id: UUID)

    fun getAll(): List<Role>

    fun getRolesByUserId(userId: String): List<Role>
}