package com.horob1.auth_service.infrastructure.repository.postgres

import com.horob1.auth_service.domain.model.permission.Permission
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PermissionPostgresRepository : JpaRepository<Permission, UUID> {
    fun findByName(name: String): Permission?

    fun existsByName(name: String): Boolean
}