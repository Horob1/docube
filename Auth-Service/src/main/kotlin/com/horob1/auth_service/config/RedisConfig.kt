package com.horob1.auth_service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

@Configuration
class RedisConfig {

    @Bean
    fun stringRedisTemplate(factory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(factory)
    }

    @Bean
    fun redisCacheManager(factory: RedisConnectionFactory): RedisCacheManager {
        val config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build()
    }
}
