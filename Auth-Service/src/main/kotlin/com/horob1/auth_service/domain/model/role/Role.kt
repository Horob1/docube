package com.horob1.auth_service.domain.model.role

import com.horob1.auth_service.domain.model.AbstractEntity
import com.horob1.auth_service.domain.model.permission.Permission
import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "tbl_roles",
    indexes = [
        Index(name = "idx_role_name", columnList = "name")
    ]
)
class Role(
    @Column(name = "name", nullable = false, unique = true)
    var name: String = "",
    @Column(name = "description")
    var description: String = "",
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "tbl_role_permission_mapping",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")]
    )
    var permissions: MutableSet<Permission> = mutableSetOf(),
) : AbstractEntity<UUID>()