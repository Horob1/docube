package com.horob1.auth_service.domain.model.user

import com.horob1.auth_service.domain.model.AbstractEntity
import com.horob1.auth_service.domain.model.role.Role
import com.horob1.auth_service.shared.enums.UserStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

@Entity
@Table(
    name = "tbl_users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["email"])
    ],
    indexes = [
        Index(name = "idx_user_status", columnList = "status")
    ]
)
class User(
    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var phoneNumber: String = "",

    @Column(nullable = false)
    var address: String = "",

    @Column(nullable = false)
    var avatar: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatus = UserStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column
    var previousStatus: UserStatus? = null,

    @Column(nullable = false)
    var is2FAEnabled: Boolean = false,

    @Column
    var totpSecret: String? = null,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "tbl_user_role_mapping",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf(),
) : AbstractEntity<UUID>()