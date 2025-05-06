package org.example.expert.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;

@EnableCaching
@Configuration
@RequiredArgsConstructor
public class CacheConfig {
	private final CaffeineProperties properties;

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager
			.setCaffeine(Caffeine.newBuilder()
			.initialCapacity(properties.getInitialCapacity())
			.maximumSize(properties.getMaximumSize())
			.expireAfterWrite(properties.getExpireAfterWrite(), TimeUnit.MINUTES)
			.expireAfterAccess(properties.getExpireAfterAccess(), TimeUnit.MINUTES)
			.recordStats());
		return cacheManager;
	}

}
