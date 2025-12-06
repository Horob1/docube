package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.domain.repository.RoleRepository
import com.horob1.auth_service.infrastructure.repository.postgres.RolePostgresRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RoleRepositoryImpl(
    private val rolePostgresRepository: RolePostgresRepository,
) : RoleRepository {
    override fun save(role: Role): Role {
        return rolePostgresRepository.save(role)
    }

    override fun delete(role: Role) {
        rolePostgresRepository.delete(role)
    }

    override fun findRoleById(id: UUID): Role? {
        return rolePostgresRepository.findById(id).orElse(null)
    }

    override fun findRoleByName(name: String): Role? {
        return rolePostgresRepository.findByName(name)
    }

    override fun findRolesByIdList(idList: List<UUID>): List<Role> {
        return rolePostgresRepository.findAllById(idList)
    }

    override fun findAll(): List<Role> {
        return rolePostgresRepository.findAll()
    }

    override fun existsByName(name: String): Boolean {
        return rolePostgresRepository.existsByName(name)
    }

    override fun saveAll(roles: List<Role>): List<Role> {
        return rolePostgresRepository.saveAll(roles)
    }

    override fun count(): Long {
        return rolePostgresRepository.count()
    }

}