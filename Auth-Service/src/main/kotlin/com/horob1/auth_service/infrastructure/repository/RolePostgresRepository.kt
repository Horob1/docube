package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.domain.model.role.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RolePostgresRepository : JpaRepository<Role, UUID> {
    fun findByName(name: String): Role?
    fun existsByName(name: String): Boolean
}