package org.example.expert.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "cache.caffeine")
@Component
data class CaffeineProperties(
    var initialCapacity: Int = 100,
    var maximumSize: Long = 1000,
    var expireAfterWrite: Long = 60,
    var expireAfterAccess: Long = 30
)
