package com.example.pipex.common.utils;

import com.example.pipex.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * API response builder với enterprise-grade features
 * - Fluent API building responses
 * - Success/error response templates
 * - Pagination metadata
 */
@Slf4j
public final class ApiResponseBuilder {

        private ApiResponseBuilder() {
                // Utility class
        }

        // ========== SUCCESS RESPONSES ==========

        /**
         * Build success response with data
         */
        public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .data(data)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built success response with data");
                return ResponseEntity.ok(response);
        }

        /**
         * Build success response with data and message
         */
        public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .data(data)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built success response with data and message: {}", message);
                return ResponseEntity.ok(response);
        }

        /**
         * Build success response with data, message and metadata
         */
        public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, Map<String, Object> metadata) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .data(data)
                                .message(message)
                                .metadata(metadata)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built success response with data, message and metadata");
                return ResponseEntity.ok(response);
        }

        /**
         * Build success response with message only
         */
        public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built success response with message: {}", message);
                return ResponseEntity.ok(response);
        }

        /**
         * Build success response with no content
         */
        public static <T> ResponseEntity<ApiResponse<T>> success() {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built success response with no content");
                return ResponseEntity.ok(response);
        }

        // ========== ERROR RESPONSES ==========

        /**
         * Build error response with message
         */
        public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built error response with message: {}", message);
                return ResponseEntity.badRequest().body(response);
        }

        /**
         * Build error response with message and error code
         */
        public static <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode(errorCode)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built error response with message and error code: {}", errorCode);
                return ResponseEntity.badRequest().body(response);
        }

        /**
         * Build error response with message, error code and details
         */
        public static <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode,
                        Map<String, Object> details) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode(errorCode)
                                .metadata(details)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built error response with message, error code and details: {}", errorCode);
                return ResponseEntity.badRequest().body(response);
        }

        /**
         * Build error response with HTTP status
         */
        public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built error response with message and status: {}", status);
                return ResponseEntity.status(status).body(response);
        }

        /**
         * Build error response with HTTP status and error code
         */
        public static <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode, HttpStatus status) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode(errorCode)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built error response with message, error code and status: {}", status);
                return ResponseEntity.status(status).body(response);
        }

        // ========== PAGINATED RESPONSES ==========

        /**
         * Build paginated success response
         */
        public static <T> ResponseEntity<ApiResponse<PaginatedData<T>>> success(Page<T> page) {
                PaginatedData<T> paginatedData = PaginatedData.<T>builder()
                                .content(page.getContent())
                                .page(page.getNumber())
                                .size(page.getSize())
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .first(page.isFirst())
                                .last(page.isLast())
                                .numberOfElements(page.getNumberOfElements())
                                .build();

                ApiResponse<PaginatedData<T>> response = ApiResponse.<PaginatedData<T>>builder()
                                .success(true)
                                .data(paginatedData)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built paginated success response: {} elements", page.getTotalElements());
                return ResponseEntity.ok(response);
        }

        /**
         * Build paginated success response with message
         */
        public static <T> ResponseEntity<ApiResponse<PaginatedData<T>>> success(Page<T> page, String message) {
                PaginatedData<T> paginatedData = PaginatedData.<T>builder()
                                .content(page.getContent())
                                .page(page.getNumber())
                                .size(page.getSize())
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .first(page.isFirst())
                                .last(page.isLast())
                                .numberOfElements(page.getNumberOfElements())
                                .build();

                ApiResponse<PaginatedData<T>> response = ApiResponse.<PaginatedData<T>>builder()
                                .success(true)
                                .data(paginatedData)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built paginated success response with message: {}", message);
                return ResponseEntity.ok(response);
        }

        /**
         * Build paginated success response with custom pagination info
         */
        public static <T> ResponseEntity<ApiResponse<PaginatedData<T>>> success(List<T> content, int page, int size,
                        long totalElements) {
                int totalPages = (int) Math.ceil((double) totalElements / size);

                PaginatedData<T> paginatedData = PaginatedData.<T>builder()
                                .content(content)
                                .page(page)
                                .size(size)
                                .totalElements(totalElements)
                                .totalPages(totalPages)
                                .first(page == 0)
                                .last(page >= totalPages - 1)
                                .numberOfElements(content.size())
                                .build();

                ApiResponse<PaginatedData<T>> response = ApiResponse.<PaginatedData<T>>builder()
                                .success(true)
                                .data(paginatedData)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built paginated success response with custom pagination info");
                return ResponseEntity.ok(response);
        }

        // ========== LIST RESPONSES ==========

        /**
         * Build success response with list data
         */
        public static <T> ResponseEntity<ApiResponse<List<T>>> success(List<T> data) {
                if (data == null) {
                        data = List.of();
                }

                ApiResponse<List<T>> response = ApiResponse.<List<T>>builder()
                                .success(true)
                                .data(data)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built success response with list data: {} elements", data.size());
                return ResponseEntity.ok(response);
        }

        /**
         * Build success response with list data and message
         */
        public static <T> ResponseEntity<ApiResponse<List<T>>> success(List<T> data, String message) {
                if (data == null) {
                        data = List.of();
                }

                ApiResponse<List<T>> response = ApiResponse.<List<T>>builder()
                                .success(true)
                                .data(data)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built success response with list data and message: {}", message);
                return ResponseEntity.ok(response);
        }

        // ========== CREATED RESPONSES ==========

        /**
         * Build created response with data
         */
        public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .data(data)
                                .message("Resource created successfully")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built created response with data");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * Build created response with data and message
         */
        public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .data(data)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built created response with data and message: {}", message);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        // ========== NO CONTENT RESPONSES ==========

        /**
         * Build no content response
         */
        public static <T> ResponseEntity<ApiResponse<T>> noContent() {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .message("No content")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built no content response");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        /**
         * Build no content response with message
         */
        public static <T> ResponseEntity<ApiResponse<T>> noContent(String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built no content response with message: {}", message);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        // ========== NOT FOUND RESPONSES ==========

        /**
         * Build not found response
         */
        public static <T> ResponseEntity<ApiResponse<T>> notFound() {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message("Resource not found")
                                .errorCode("NOT_FOUND")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built not found response");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        /**
         * Build not found response with message
         */
        public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode("NOT_FOUND")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built not found response with message: {}", message);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // ========== UNAUTHORIZED RESPONSES ==========

        /**
         * Build unauthorized response
         */
        public static <T> ResponseEntity<ApiResponse<T>> unauthorized() {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message("Unauthorized access")
                                .errorCode("UNAUTHORIZED")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built unauthorized response");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        /**
         * Build unauthorized response with message
         */
        public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode("UNAUTHORIZED")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built unauthorized response with message: {}", message);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // ========== FORBIDDEN RESPONSES ==========

        /**
         * Build forbidden response
         */
        public static <T> ResponseEntity<ApiResponse<T>> forbidden() {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message("Access forbidden")
                                .errorCode("FORBIDDEN")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built forbidden response");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        /**
         * Build forbidden response with message
         */
        public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode("FORBIDDEN")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built forbidden response with message: {}", message);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // ========== INTERNAL SERVER ERROR RESPONSES ==========

        /**
         * Build internal server error response
         */
        public static <T> ResponseEntity<ApiResponse<T>> internalServerError() {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message("Internal server error")
                                .errorCode("INTERNAL_SERVER_ERROR")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built internal server error response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        /**
         * Build internal server error response with message
         */
        public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode("INTERNAL_SERVER_ERROR")
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built internal server error response with message: {}", message);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // ========== VALIDATION ERROR RESPONSES ==========

        /**
         * Build validation error response
         */
        public static <T> ResponseEntity<ApiResponse<T>> validationError(String message,
                        Map<String, Object> validationErrors) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode("VALIDATION_ERROR")
                                .metadata(validationErrors)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built validation error response with message: {}", message);
                return ResponseEntity.badRequest().body(response);
        }

        /**
         * Build validation error response with error code
         */
        public static <T> ResponseEntity<ApiResponse<T>> validationError(String message, String errorCode,
                        Map<String, Object> validationErrors) {
                ApiResponse<T> response = ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errorCode(errorCode)
                                .metadata(validationErrors)
                                .timestamp(LocalDateTime.now())
                                .build();

                log.debug("Built validation error response with message and error code: {}", errorCode);
                return ResponseEntity.badRequest().body(response);
        }
}
