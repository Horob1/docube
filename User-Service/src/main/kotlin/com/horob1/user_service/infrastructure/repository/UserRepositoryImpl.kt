package com.horob1.user_service.infrastructure.repository

import com.horob1.user_service.domain.user.User
import com.horob1.user_service.domain.user.UserRepository
import com.horob1.user_service.domain.user.UserSummary
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userMongoRepository: UserMongoRepository,
) : UserRepository {
    override fun save(user: User): UserSummary {
        return userMongoRepository.save(user).toUserSummary()
    }

    override fun isExisted(email: String): Boolean {
        val user = userMongoRepository.findByEmail(email)
        return user != null
    }

    override fun findById(id: String): User? {
        return userMongoRepository.findById(id).orElse(null)
    }

    override fun findByEmail(email: String): User? {
        return userMongoRepository.findByEmail(email)
    }
}