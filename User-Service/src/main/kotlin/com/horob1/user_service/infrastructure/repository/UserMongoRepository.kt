package com.horob1.user_service.infrastructure.repository

import com.horob1.user_service.domain.user.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserMongoRepository : MongoRepository<User, String> {
    fun findByEmail(email: String): User?
}