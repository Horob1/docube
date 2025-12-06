package com.horob1.auth_service.domain.repository

import com.horob1.auth_service.domain.model.role.Role
import java.util.*

interface RoleRepository {

    fun save(role: Role): Role

    fun delete(role: Role)

    fun findRoleById(id: UUID): Role?

    fun findRoleByName(name: String): Role?

    fun findRolesByIdList(idList: List<UUID>): List<Role>

    fun findAll(): List<Role>

    fun existsByName(name: String): Boolean

    fun saveAll(roles: List<Role>): List<Role>

    fun count(): Long
}