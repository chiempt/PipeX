package com.example.pipex.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Cache utilities với enterprise-grade features
 * - Spring bean với Redis integration
 * - Generic cache operations
 * - TTL management
 * - Cache eviction strategies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    // ========== BASIC CACHE OPERATIONS ==========

    /**
     * Put value in cache
     */
    public void put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Cached value for key: {}", key);
        } catch (Exception e) {
            log.error("Failed to cache value for key {}: {}", key, e.getMessage(), e);
        }
    }

    /**
     * Put value in cache with TTL
     */
    public void put(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
            log.debug("Cached value for key: {} with TTL: {} {}", key, ttl, timeUnit);
        } catch (Exception e) {
            log.error("Failed to cache value for key {} with TTL: {}", key, e.getMessage(), e);
        }
    }

    /**
     * Put value in cache with Duration
     */
    public void put(String key, Object value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, value, duration);
            log.debug("Cached value for key: {} with duration: {}", key, duration);
        } catch (Exception e) {
            log.error("Failed to cache value for key {} with duration: {}", key, e.getMessage(), e);
        }
    }

    /**
     * Get value from cache
     */
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved value for key: {}", key);
            return value;
        } catch (Exception e) {
            log.error("Failed to get value for key {}: {}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get value from cache with type casting
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && clazz.isAssignableFrom(value.getClass())) {
                return (T) value;
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to get value for key {} with type {}: {}", key, clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get value from cache or compute if not present
     */
    public <T> T getOrCompute(String key, java.util.function.Supplier<T> supplier) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                @SuppressWarnings("unchecked")
                T result = (T) value;
                return result;
            }

            T computedValue = supplier.get();
            if (computedValue != null) {
                put(key, computedValue);
            }
            return computedValue;
        } catch (Exception e) {
            log.error("Failed to get or compute value for key {}: {}", key, e.getMessage(), e);
            return supplier.get();
        }
    }

    /**
     * Get value from cache or compute if not present with TTL
     */
    public <T> T getOrCompute(String key, java.util.function.Supplier<T> supplier, long ttl, TimeUnit timeUnit) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                @SuppressWarnings("unchecked")
                T result = (T) value;
                return result;
            }

            T computedValue = supplier.get();
            if (computedValue != null) {
                put(key, computedValue, ttl, timeUnit);
            }
            return computedValue;
        } catch (Exception e) {
            log.error("Failed to get or compute value for key {} with TTL: {}", key, e.getMessage(), e);
            return supplier.get();
        }
    }

    // ========== CACHE EXISTENCE ==========

    /**
     * Check if key exists in cache
     */
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Failed to check existence for key {}: {}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if multiple keys exist
     */
    public boolean exists(String... keys) {
        try {
            if (keys == null || keys.length == 0)
                return false;
            return redisTemplate.hasKey(keys[0]);
        } catch (Exception e) {
            log.error("Failed to check existence for keys: {}", e.getMessage(), e);
            return false;
        }
    }

    // ========== CACHE DELETION ==========

    /**
     * Delete key from cache
     */
    public boolean delete(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Deleted key: {}", key);
            return deleted != null && deleted;
        } catch (Exception e) {
            log.error("Failed to delete key {}: {}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Delete multiple keys from cache
     */
    public long delete(String... keys) {
        try {
            if (keys == null || keys.length == 0)
                return 0;
            Long deleted = redisTemplate.delete(List.of(keys));
            log.debug("Deleted {} keys", deleted);
            return deleted != null ? deleted : 0;
        } catch (Exception e) {
            log.error("Failed to delete keys: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Delete keys by pattern
     */
    public long deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                Long deleted = redisTemplate.delete(keys);
                log.debug("Deleted {} keys matching pattern: {}", deleted, pattern);
                return deleted != null ? deleted : 0;
            }
            return 0;
        } catch (Exception e) {
            log.error("Failed to delete keys by pattern {}: {}", pattern, e.getMessage(), e);
            return 0;
        }
    }

    // ========== TTL OPERATIONS ==========

    /**
     * Set TTL for key
     */
    public boolean expire(String key, long ttl, TimeUnit timeUnit) {
        try {
            Boolean expired = redisTemplate.expire(key, ttl, timeUnit);
            log.debug("Set TTL for key: {} to {} {}", key, ttl, timeUnit);
            return expired != null && expired;
        } catch (Exception e) {
            log.error("Failed to set TTL for key {}: {}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Set TTL for key with Duration
     */
    public boolean expire(String key, Duration duration) {
        try {
            Boolean expired = redisTemplate.expire(key, duration);
            log.debug("Set TTL for key: {} to {}", key, duration);
            return expired != null && expired;
        } catch (Exception e) {
            log.error("Failed to set TTL for key {}: {}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get TTL for key
     */
    public long getTtl(String key) {
        try {
            Long ttl = redisTemplate.getExpire(key);
            return ttl != null ? ttl : -1;
        } catch (Exception e) {
            log.error("Failed to get TTL for key {}: {}", key, e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Get TTL for key in specific time unit
     */
    public long getTtl(String key, TimeUnit timeUnit) {
        try {
            Long ttl = redisTemplate.getExpire(key, timeUnit);
            return ttl != null ? ttl : -1;
        } catch (Exception e) {
            log.error("Failed to get TTL for key {} in {}: {}", key, timeUnit, e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Remove TTL from key (make it persistent)
     */
    public boolean persist(String key) {
        try {
            Boolean persisted = redisTemplate.persist(key);
            log.debug("Made key persistent: {}", key);
            return persisted != null && persisted;
        } catch (Exception e) {
            log.error("Failed to persist key {}: {}", key, e.getMessage(), e);
            return false;
        }
    }

    // ========== BATCH OPERATIONS ==========

    /**
     * Put multiple key-value pairs
     */
    public void putAll(Map<String, Object> keyValueMap) {
        try {
            redisTemplate.opsForValue().multiSet(keyValueMap);
            log.debug("Cached {} key-value pairs", keyValueMap.size());
        } catch (Exception e) {
            log.error("Failed to cache multiple key-value pairs: {}", e.getMessage(), e);
        }
    }

    /**
     * Put multiple key-value pairs with TTL
     */
    public void putAll(Map<String, Object> keyValueMap, long ttl, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().multiSet(keyValueMap);
            // Set TTL for all keys
            for (String key : keyValueMap.keySet()) {
                expire(key, ttl, timeUnit);
            }
            log.debug("Cached {} key-value pairs with TTL: {} {}", keyValueMap.size(), ttl, timeUnit);
        } catch (Exception e) {
            log.error("Failed to cache multiple key-value pairs with TTL: {}", e.getMessage(), e);
        }
    }

    /**
     * Get multiple values
     */
    public List<Object> getAll(Collection<String> keys) {
        try {
            List<Object> values = redisTemplate.opsForValue().multiGet(keys);
            log.debug("Retrieved {} values", values != null ? values.size() : 0);
            return values;
        } catch (Exception e) {
            log.error("Failed to get multiple values: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Get multiple values with type casting
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Collection<String> keys, Class<T> clazz) {
        try {
            List<Object> values = redisTemplate.opsForValue().multiGet(keys);
            if (values == null)
                return List.of();

            return values.stream()
                    .filter(value -> value != null && clazz.isAssignableFrom(value.getClass()))
                    .map(value -> (T) value)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get multiple values with type {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            return List.of();
        }
    }

    // ========== ATOMIC OPERATIONS ==========

    /**
     * Increment value atomically
     */
    public long increment(String key) {
        try {
            Long value = redisTemplate.opsForValue().increment(key);
            log.debug("Incremented value for key: {}", key);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("Failed to increment value for key {}: {}", key, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Increment value by delta atomically
     */
    public long increment(String key, long delta) {
        try {
            Long value = redisTemplate.opsForValue().increment(key, delta);
            log.debug("Incremented value for key: {} by {}", key, delta);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("Failed to increment value for key {} by {}: {}", key, delta, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Decrement value atomically
     */
    public long decrement(String key) {
        try {
            Long value = redisTemplate.opsForValue().decrement(key);
            log.debug("Decremented value for key: {}", key);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("Failed to decrement value for key {}: {}", key, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Decrement value by delta atomically
     */
    public long decrement(String key, long delta) {
        try {
            Long value = redisTemplate.opsForValue().decrement(key, delta);
            log.debug("Decremented value for key: {} by {}", key, delta);
            return value != null ? value : 0;
        } catch (Exception e) {
            log.error("Failed to decrement value for key {} by {}: {}", key, delta, e.getMessage(), e);
            return 0;
        }
    }

    // ========== CACHE STATISTICS ==========

    /**
     * Get cache size (number of keys)
     */
    public long size() {
        try {
            if (redisTemplate.getConnectionFactory() != null) {
                var connection = redisTemplate.getConnectionFactory().getConnection();
                if (connection != null) {
                    Long size = connection.dbSize();
                    return size != null ? size : 0;
                }
            }
            return 0;
        } catch (Exception e) {
            log.error("Failed to get cache size: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Get all keys matching pattern
     */
    public Set<String> getKeys(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            return keys != null ? keys : Set.of();
        } catch (Exception e) {
            log.error("Failed to get keys matching pattern {}: {}", pattern, e.getMessage(), e);
            return Set.of();
        }
    }

    /**
     * Get all keys
     */
    public Set<String> getAllKeys() {
        return getKeys("*");
    }

    // ========== CACHE EVICTION ==========

    /**
     * Clear all cache
     */
    public void clear() {
        try {
            if (redisTemplate.getConnectionFactory() != null) {
                var connection = redisTemplate.getConnectionFactory().getConnection();
                if (connection != null) {
                    connection.flushDb();
                    log.info("Cleared all cache");
                }
            }
        } catch (Exception e) {
            log.error("Failed to clear cache: {}", e.getMessage(), e);
        }
    }

    /**
     * Clear cache by pattern
     */
    public void clearByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("Failed to clear cache by pattern {}: {}", pattern, e.getMessage(), e);
        }
    }

    // ========== UTILITY METHODS ==========

    /**
     * Get Redis template
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * Check if cache is available
     */
    public boolean isAvailable() {
        try {
            if (redisTemplate.getConnectionFactory() != null) {
                var connection = redisTemplate.getConnectionFactory().getConnection();
                if (connection != null) {
                    connection.ping();
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Cache is not available: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get cache info
     */
    public Map<String, Object> getCacheInfo() {
        try {
            Map<String, Object> info = Map.of(
                    "size", size(),
                    "available", isAvailable(),
                    "keys", getAllKeys().size());
            return info;
        } catch (Exception e) {
            log.error("Failed to get cache info: {}", e.getMessage(), e);
            return Map.of("error", e.getMessage());
        }
    }
}
