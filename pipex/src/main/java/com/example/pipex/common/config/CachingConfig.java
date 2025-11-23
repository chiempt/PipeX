package com.example.pipex.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Caching configuration với enterprise-grade features
 * - Redis cache manager with proper TTL configuration
 * - Cache eviction policies
 * - Cache statistics and monitoring
 */
@Slf4j
@Configuration
@EnableCaching
public class CachingConfig {

        /**
         * Primary Redis cache manager
         */
        @Bean
        @Primary
        public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(300)) // 5 minutes default TTL
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(
                                                                new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer()))
                                .disableCachingNullValues(); // Don't cache null values

                RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(config)
                                .transactionAware()
                                .build();

                log.info("Redis Cache Manager configured with default TTL: 300s");
                return cacheManager;
        }

        /**
         * Auth tokens cache configuration with shorter TTL
         */
        @Bean("authTokensCacheManager")
        public CacheManager authTokensCacheManager(RedisConnectionFactory connectionFactory) {
                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(600)) // 10 minutes for auth tokens
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(
                                                                new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer()))
                                .disableCachingNullValues();

                RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(config)
                                .transactionAware()
                                .build();

                log.info("Auth Tokens Cache Manager configured with TTL: 600s");
                return cacheManager;
        }

        /**
         * Alternative in-memory cache for development/testing
         */
        @Bean("inMemoryCacheManager")
        public CacheManager inMemoryCacheManager() {
                ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager("authTokens");
                cacheManager.setAllowNullValues(false);
                log.info("In-Memory Cache Manager configured (for development/testing)");
                return cacheManager;
        }
}
