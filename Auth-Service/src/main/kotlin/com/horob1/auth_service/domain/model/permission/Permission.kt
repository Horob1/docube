package com.horob1.auth_service.domain.model.permission

import com.horob1.auth_service.domain.model.AbstractEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(
    name = "tbl_permissions",
    indexes = [
        Index(name = "idx_permission_name", columnList = "name")
    ]
)
class Permission(
    @Column(name = "name", unique = true, nullable = false)
    var name: String = "",
    @Column(name = "description")
    var description: String = "",
) : AbstractEntity<UUID>()