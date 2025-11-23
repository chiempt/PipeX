package com.example.pipex.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Object manipulation utilities với enterprise-grade features
 * - Null-safe operations
 * - Deep clone và merge
 * - Property access với reflection
 * - Type conversion utilities
 */
@Slf4j
public final class ObjectUtils {

    private ObjectUtils() {
        // Utility class
    }

    // ========== NULL SAFETY ==========

    /**
     * Check if object is null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * Check if object is not null
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * Check if object is empty (null, empty string, empty collection)
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof String)
            return ((String) obj).isEmpty();
        if (obj instanceof Collection)
            return ((Collection<?>) obj).isEmpty();
        if (obj instanceof Map)
            return ((Map<?, ?>) obj).isEmpty();
        if (obj.getClass().isArray())
            return ((Object[]) obj).length == 0;
        return false;
    }

    /**
     * Check if object is not empty
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * Return default value if object is null
     */
    public static <T> T defaultIfNull(T obj, T defaultValue) {
        return obj != null ? obj : defaultValue;
    }

    /**
     * Return default value if object is empty
     */
    public static <T> T defaultIfEmpty(T obj, T defaultValue) {
        return !isEmpty(obj) ? obj : defaultValue;
    }

    // ========== TYPE CONVERSION ==========

    /**
     * Safe cast with default value
     */
    @SuppressWarnings("unchecked")
    public static <T> T safeCast(Object obj, Class<T> targetClass, T defaultValue) {
        try {
            if (obj != null && targetClass.isAssignableFrom(obj.getClass())) {
                return (T) obj;
            }
        } catch (Exception e) {
            log.warn("Failed to cast object to {}: {}", targetClass.getSimpleName(), e.getMessage());
        }
        return defaultValue;
    }

    /**
     * Convert object to string safely
     */
    public static String toString(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    /**
     * Convert object to string with default
     */
    public static String toString(Object obj, String defaultValue) {
        return obj != null ? obj.toString() : defaultValue;
    }

    // ========== DEEP OPERATIONS ==========

    /**
     * Deep clone object using serialization
     */
    public static <T> T deepClone(T obj) {
        if (obj == null)
            return null;

        try {
            // Simple implementation - can be enhanced with proper serialization
            if (obj instanceof Cloneable) {
                Method cloneMethod = obj.getClass().getMethod("clone");
                cloneMethod.setAccessible(true);
                return (T) cloneMethod.invoke(obj);
            }
        } catch (Exception e) {
            log.warn("Failed to clone object: {}", e.getMessage());
        }

        return obj; // Return original if cloning fails
    }

    /**
     * Merge two objects (shallow merge)
     */
    public static <T> T merge(T source, T target) {
        if (source == null)
            return target;
        if (target == null)
            return source;

        try {
            Field[] fields = source.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object sourceValue = field.get(source);
                if (sourceValue != null) {
                    field.set(target, sourceValue);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to merge objects: {}", e.getMessage());
        }

        return target;
    }

    /**
     * Compare two objects deeply
     */
    public static boolean deepEquals(Object obj1, Object obj2) {
        if (obj1 == obj2)
            return true;
        if (obj1 == null || obj2 == null)
            return false;

        if (obj1 instanceof Collection && obj2 instanceof Collection) {
            return ((Collection<?>) obj1).equals((Collection<?>) obj2);
        }

        if (obj1 instanceof Map && obj2 instanceof Map) {
            return ((Map<?, ?>) obj1).equals((Map<?, ?>) obj2);
        }

        return obj1.equals(obj2);
    }

    // ========== PROPERTY ACCESS ==========

    /**
     * Get property value by name
     */
    public static Object getProperty(Object obj, String propertyName) {
        if (obj == null || propertyName == null)
            return null;

        try {
            Field field = findField(obj.getClass(), propertyName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(obj);
            }
        } catch (Exception e) {
            log.warn("Failed to get property {}: {}", propertyName, e.getMessage());
        }

        return null;
    }

    /**
     * Set property value by name
     */
    public static void setProperty(Object obj, String propertyName, Object value) {
        if (obj == null || propertyName == null)
            return;

        try {
            Field field = findField(obj.getClass(), propertyName);
            if (field != null) {
                field.setAccessible(true);
                field.set(obj, value);
            }
        } catch (Exception e) {
            log.warn("Failed to set property {}: {}", propertyName, e.getMessage());
        }
    }

    /**
     * Find field in class hierarchy
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    // ========== COLLECTION UTILITIES ==========

    /**
     * Convert object to list
     */
    public static List<Object> toList(Object obj) {
        if (obj == null)
            return Collections.emptyList();
        if (obj instanceof Collection)
            return new ArrayList<>((Collection<?>) obj);
        if (obj.getClass().isArray())
            return Arrays.asList((Object[]) obj);
        return Collections.singletonList(obj);
    }

    /**
     * Convert object to map
     */
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null)
            return Collections.emptyMap();
        if (obj instanceof Map)
            return new HashMap<>((Map<String, Object>) obj);

        Map<String, Object> map = new HashMap<>();
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            log.warn("Failed to convert object to map: {}", e.getMessage());
        }

        return map;
    }

    // ========== FUNCTIONAL UTILITIES ==========

    /**
     * Apply function if object is not null
     */
    public static <T, R> R applyIfNotNull(T obj, Function<T, R> function) {
        return obj != null ? function.apply(obj) : null;
    }

    /**
     * Apply function if object is not null, otherwise return default
     */
    public static <T, R> R applyIfNotNull(T obj, Function<T, R> function, R defaultValue) {
        return obj != null ? function.apply(obj) : defaultValue;
    }

    /**
     * Filter object based on predicate
     */
    public static <T> Optional<T> filter(T obj, Predicate<T> predicate) {
        return obj != null && predicate.test(obj) ? Optional.of(obj) : Optional.empty();
    }

    // ========== VALIDATION UTILITIES ==========

    /**
     * Check if object is instance of given class
     */
    public static boolean isInstanceOf(Object obj, Class<?> clazz) {
        return obj != null && clazz.isAssignableFrom(obj.getClass());
    }

    /**
     * Check if object is assignable to given class
     */
    public static boolean isAssignableTo(Object obj, Class<?> clazz) {
        return obj != null && clazz.isAssignableFrom(obj.getClass());
    }

    /**
     * Get class name safely
     */
    public static String getClassName(Object obj) {
        return obj != null ? obj.getClass().getSimpleName() : "null";
    }

    /**
     * Get full class name safely
     */
    public static String getFullClassName(Object obj) {
        return obj != null ? obj.getClass().getName() : "null";
    }
}
