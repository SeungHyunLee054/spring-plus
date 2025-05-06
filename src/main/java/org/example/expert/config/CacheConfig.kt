package org.example.expert.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@EnableCaching
@Configuration
class CacheConfig(private val properties: CaffeineProperties) {

    @Bean
    fun cacheManager(): CacheManager = CaffeineCacheManager().apply {
        setCaffeine(
            Caffeine.newBuilder()
                .initialCapacity(properties.initialCapacity)
                .maximumSize(properties.maximumSize)
                .expireAfterWrite(properties.expireAfterWrite, TimeUnit.MINUTES)
                .expireAfterAccess(properties.expireAfterAccess, TimeUnit.MINUTES)
                .recordStats()
        )
    }
}
