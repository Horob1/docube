package com.horob1.auth_service.infrastructure.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration

@Service
class RedisStringService(
    private val redisStringTemplate: StringRedisTemplate,
) {
    // Key - value
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
