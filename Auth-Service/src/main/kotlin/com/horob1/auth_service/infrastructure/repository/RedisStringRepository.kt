package com.horob1.auth_service.infrastructure.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisStringRepository(
    private val redisStringTemplate: StringRedisTemplate,
) {
    fun saveValue(key: String, value: String, ttl: Duration = Duration.ofMinutes(10)) {
        redisStringTemplate.opsForValue().set(key, value, ttl)
    }

    fun getValue(key: String): String? {
        return redisStringTemplate.opsForValue()[key]
    }

    fun deleteValue(key: String) {
        redisStringTemplate.delete(key)
    }
}
