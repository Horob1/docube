package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.domain.model.user_role_mapping.UserRoleMapping
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface URMPostgresRepository : JpaRepository<UserRoleMapping, UUID> {
    fun getAllByUserId(userId: String): MutableList<UserRoleMapping>
}