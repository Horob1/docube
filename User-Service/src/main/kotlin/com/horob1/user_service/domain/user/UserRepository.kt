package com.horob1.user_service.domain.user

interface UserRepository {
    fun save(user: User): UserSummary

    fun isExisted(email: String): Boolean

    fun findById(id: String): User?

    fun findByEmail(email: String): User?
}