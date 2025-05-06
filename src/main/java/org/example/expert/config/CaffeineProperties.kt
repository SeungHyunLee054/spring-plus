package org.example.expert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cache.caffeine")
@Component
@Getter
@Setter
public class CaffeineProperties {
	private int initialCapacity;
	private int maximumSize ;
	private int expireAfterWrite;
	private int expireAfterAccess;

}
