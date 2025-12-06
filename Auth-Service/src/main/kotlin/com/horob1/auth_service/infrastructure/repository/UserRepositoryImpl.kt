package com.horob1.auth_service.infrastructure.repository

import com.horob1.auth_service.domain.model.user.User
import com.horob1.auth_service.domain.repository.UserRepository
import com.horob1.auth_service.infrastructure.repository.postgres.UserPostgresRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryImpl(
    private val userPostgresRepository: UserPostgresRepository
) : UserRepository {
    override fun findByEmail(email: String): User? {
        return userPostgresRepository.findByEmail(email)
    }

    override fun findById(id: UUID): User? {
        return userPostgresRepository.findById(id).orElse(null)
    }

    override fun save(user: User): User {
        return userPostgresRepository.save(user)
    }

    override fun isExists(email: String): Boolean {
        return userPostgresRepository.existsByEmail(email)
    }

    override fun count(): Long {
        return userPostgresRepository.count()
    }
}