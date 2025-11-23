package com.example.pipex.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Generic API response wrapper với enterprise-grade features
 * - Consistent response structure for all APIs
 * - Success/error indication
 * - Timestamp và metadata support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private String errorCode;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;

    // ========== UTILITY METHODS ==========

    /**
     * Check if response is successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Check if response has data
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * Check if response has message
     */
    public boolean hasMessage() {
        return message != null && !message.trim().isEmpty();
    }

    /**
     * Check if response has error code
     */
    public boolean hasErrorCode() {
        return errorCode != null && !errorCode.trim().isEmpty();
    }

    /**
     * Check if response has metadata
     */
    public boolean hasMetadata() {
        return metadata != null && !metadata.isEmpty();
    }

    /**
     * Get data safely
     */
    public T getData() {
        return data;
    }

    /**
     * Get message safely
     */
    public String getMessage() {
        return message != null ? message : "";
    }

    /**
     * Get error code safely
     */
    public String getErrorCode() {
        return errorCode != null ? errorCode : "";
    }

    /**
     * Get metadata safely
     */
    public Map<String, Object> getMetadata() {
        return metadata != null ? metadata : Map.of();
    }

    /**
     * Get timestamp safely
     */
    public LocalDateTime getTimestamp() {
        return timestamp != null ? timestamp : LocalDateTime.now();
    }

    // ========== FACTORY METHODS ==========

    /**
     * Create success response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create success response with message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create success response with message and metadata
     */
    public static <T> ApiResponse<T> success(T data, String message, Map<String, Object> metadata) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response with error code
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response with error code and metadata
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, Map<String, Object> metadata) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ========== STRING REPRESENTATION ==========

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ApiResponse{");
        sb.append("success=").append(success);

        if (hasData()) {
            sb.append(", data=").append(data);
        }

        if (hasMessage()) {
            sb.append(", message='").append(message).append("'");
        }

        if (hasErrorCode()) {
            sb.append(", errorCode='").append(errorCode).append("'");
        }

        if (hasMetadata()) {
            sb.append(", metadata=").append(metadata);
        }

        sb.append(", timestamp=").append(timestamp);
        sb.append("}");

        return sb.toString();
    }
}