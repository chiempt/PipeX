package com.example.pipex.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * JSON manipulation utilities với enterprise-grade features
 * - Spring bean với ObjectMapper injection
 * - Serialize/deserialize với error handling
 * - Pretty print, minify
 * - JSON path operations
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public JsonUtils() {
        this.objectMapper = new ObjectMapper();
        configureObjectMapper();
    }

    private void configureObjectMapper() {
        // Configure ObjectMapper for better performance and error handling
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                false);

        // Register Java Time module for LocalDateTime support
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ========== SERIALIZATION ==========

    /**
     * Serialize object to JSON string
     */
    public String toJson(Object obj) {
        try {
            return obj != null ? objectMapper.writeValueAsString(obj) : "null";
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * Serialize object to JSON string with pretty print
     */
    public String toPrettyJson(Object obj) {
        try {
            return obj != null ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj) : "null";
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to pretty JSON: {}", e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * Serialize object to JSON bytes
     */
    public byte[] toJsonBytes(Object obj) {
        try {
            return obj != null ? objectMapper.writeValueAsBytes(obj) : new byte[0];
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON bytes: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * Serialize object to Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap(Object obj) {
        try {
            return obj != null ? objectMapper.convertValue(obj, Map.class) : Map.of();
        } catch (Exception e) {
            log.error("Failed to convert object to Map: {}", e.getMessage(), e);
            return Map.of();
        }
    }

    // ========== DESERIALIZATION ==========

    /**
     * Deserialize JSON string to object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return json != null ? objectMapper.readValue(json, clazz) : null;
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deserialize JSON string to object with TypeReference
     */
    public <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return json != null ? objectMapper.readValue(json, typeReference) : null;
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to TypeReference: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deserialize JSON bytes to object
     */
    public <T> T fromJsonBytes(byte[] jsonBytes, Class<T> clazz) {
        try {
            return jsonBytes != null ? objectMapper.readValue(jsonBytes, clazz) : null;
        } catch (Exception e) {
            log.error("Failed to deserialize JSON bytes to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deserialize JSON string to Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> fromJsonToMap(String json) {
        try {
            return json != null ? objectMapper.readValue(json, Map.class) : Map.of();
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to Map: {}", e.getMessage(), e);
            return Map.of();
        }
    }

    // ========== CONVERSION ==========

    /**
     * Convert object to another type
     */
    public <T> T convert(Object obj, Class<T> clazz) {
        try {
            return obj != null ? objectMapper.convertValue(obj, clazz) : null;
        } catch (Exception e) {
            log.error("Failed to convert object to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convert object to another type with TypeReference
     */
    public <T> T convert(Object obj, TypeReference<T> typeReference) {
        try {
            return obj != null ? objectMapper.convertValue(obj, typeReference) : null;
        } catch (Exception e) {
            log.error("Failed to convert object to TypeReference: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deep copy object using JSON serialization
     */
    public <T> T deepCopy(T obj, Class<T> clazz) {
        try {
            String json = toJson(obj);
            return fromJson(json, clazz);
        } catch (Exception e) {
            log.error("Failed to deep copy object: {}", e.getMessage(), e);
            return null;
        }
    }

    // ========== VALIDATION ==========

    /**
     * Check if string is valid JSON
     */
    public boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty())
            return false;

        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * Check if string is valid JSON object
     */
    public boolean isValidJsonObject(String json) {
        if (json == null || json.trim().isEmpty())
            return false;

        try {
            return objectMapper.readTree(json).isObject();
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * Check if string is valid JSON array
     */
    public boolean isValidJsonArray(String json) {
        if (json == null || json.trim().isEmpty())
            return false;

        try {
            return objectMapper.readTree(json).isArray();
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    // ========== MINIFICATION ==========

    /**
     * Minify JSON string
     */
    public String minify(String json) {
        try {
            if (json == null)
                return null;
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to minify JSON: {}", e.getMessage(), e);
            return json;
        }
    }

    /**
     * Pretty print JSON string
     */
    public String prettyPrint(String json) {
        try {
            if (json == null)
                return null;
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to pretty print JSON: {}", e.getMessage(), e);
            return json;
        }
    }

    // ========== JSON PATH OPERATIONS ==========

    /**
     * Get value from JSON by path
     */
    public Object getValueByPath(String json, String path) {
        try {
            if (json == null || path == null)
                return null;

            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(json);
            String[] pathParts = path.split("\\.");

            com.fasterxml.jackson.databind.JsonNode currentNode = rootNode;
            for (String part : pathParts) {
                if (currentNode == null)
                    return null;
                currentNode = currentNode.get(part);
            }

            return currentNode != null ? objectMapper.treeToValue(currentNode, Object.class) : null;
        } catch (Exception e) {
            log.error("Failed to get value by path '{}': {}", path, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Set value in JSON by path
     */
    public String setValueByPath(String json, String path, Object value) {
        try {
            if (json == null || path == null)
                return json;

            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(json);
            String[] pathParts = path.split("\\.");

            com.fasterxml.jackson.databind.JsonNode currentNode = rootNode;
            for (int i = 0; i < pathParts.length - 1; i++) {
                String part = pathParts[i];
                if (currentNode.get(part) == null) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) currentNode).set(part,
                            objectMapper.createObjectNode());
                }
                currentNode = currentNode.get(part);
            }

            String lastPart = pathParts[pathParts.length - 1];
            ((com.fasterxml.jackson.databind.node.ObjectNode) currentNode).set(lastPart,
                    objectMapper.valueToTree(value));

            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            log.error("Failed to set value by path '{}': {}", path, e.getMessage(), e);
            return json;
        }
    }

    // ========== MERGE OPERATIONS ==========

    /**
     * Merge two JSON objects
     */
    public String mergeJson(String json1, String json2) {
        try {
            if (json1 == null)
                return json2;
            if (json2 == null)
                return json1;

            Map<String, Object> map1 = fromJsonToMap(json1);
            Map<String, Object> map2 = fromJsonToMap(json2);

            map1.putAll(map2);
            return toJson(map1);
        } catch (Exception e) {
            log.error("Failed to merge JSON objects: {}", e.getMessage(), e);
            return json1;
        }
    }

    /**
     * Merge multiple JSON objects
     */
    public String mergeJson(String... jsonStrings) {
        if (jsonStrings == null || jsonStrings.length == 0)
            return "{}";

        String result = jsonStrings[0];
        for (int i = 1; i < jsonStrings.length; i++) {
            result = mergeJson(result, jsonStrings[i]);
        }
        return result;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Get ObjectMapper instance
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Create new ObjectMapper with custom configuration
     */
    public ObjectMapper createCustomObjectMapper() {
        ObjectMapper customMapper = new ObjectMapper();
        configureObjectMapper();
        return customMapper;
    }

    /**
     * Get JSON size in bytes
     */
    public int getJsonSize(String json) {
        return json != null ? json.getBytes().length : 0;
    }

    /**
     * Get JSON size in characters
     */
    public int getJsonLength(String json) {
        return json != null ? json.length() : 0;
    }

    /**
     * Check if JSON is empty
     */
    public boolean isEmpty(String json) {
        return json == null || json.trim().isEmpty() || json.trim().equals("{}") || json.trim().equals("[]");
    }

    /**
     * Check if JSON is not empty
     */
    public boolean isNotEmpty(String json) {
        return !isEmpty(json);
    }
}
