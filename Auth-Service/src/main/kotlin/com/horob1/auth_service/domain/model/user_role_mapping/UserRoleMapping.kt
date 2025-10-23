package com.horob1.auth_service.domain.model.user_role_mapping

import com.horob1.auth_service.domain.model.AbstractEntity
import com.horob1.auth_service.domain.model.role.Role
import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "tbl_user_role_mapping",
    indexes = [
        Index(name = "idx_user_role_mapping_user_id", columnList = "user_id")
    ]
)
class UserRoleMapping(
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,
    @Column(name = "user_id", nullable = false)
    var userId: String
) : AbstractEntity<UUID>()