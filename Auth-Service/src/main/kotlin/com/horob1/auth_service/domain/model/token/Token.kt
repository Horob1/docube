package com.horob1.auth_service.domain.model.token

import com.horob1.auth_service.domain.model.AbstractEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "tbl_token",
    indexes = [
        Index(name = "idx_token_user_id", columnList = "user_id")
    ]
)
class Token(
    @Column(name = "user_id", nullable = false)
    var userId: String,

    @Column(name = "token", nullable = false)
    var token: String,

    @Column(name = "ua", nullable = false)
    var ua: String,
) : AbstractEntity<UUID>()