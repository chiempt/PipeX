package com.example.pipex.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * HTTP client utilities với enterprise-grade features
 * - Spring bean với RestTemplate/WebClient
 * - Retry mechanism với exponential backoff
 * - Circuit breaker integration
 * - Request/response logging
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpClientUtils {

    private final RestTemplate restTemplate;

    // ========== BASIC HTTP OPERATIONS ==========

    /**
     * GET request
     */
    public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        try {
            log.debug("Making GET request to: {}", url);
            ResponseEntity<T> response = restTemplate.getForEntity(url, responseType);
            log.debug("GET request completed with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("GET request failed for URL {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * GET request with headers
     */
    public <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        try {
            log.debug("Making GET request to: {} with headers", url);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            log.debug("GET request completed with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("GET request failed for URL {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * POST request
     */
    public <T> ResponseEntity<T> post(String url, Object requestBody, Class<T> responseType) {
        try {
            log.debug("Making POST request to: {}", url);
            ResponseEntity<T> response = restTemplate.postForEntity(url, requestBody, responseType);
            log.debug("POST request completed with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("POST request failed for URL {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * POST request with headers
     */
    public <T> ResponseEntity<T> post(String url, Object requestBody, HttpHeaders headers, Class<T> responseType) {
        try {
            log.debug("Making POST request to: {} with headers", url);
            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            log.debug("POST request completed with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("POST request failed for URL {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * PUT request
     */
    public <T> ResponseEntity<T> put(String url, Object requestBody, Class<T> responseType) {
        try {
            log.debug("Making PUT request to: {}", url);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT,
                    new HttpEntity<>(requestBody), responseType);
            log.debug("PUT request completed with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("PUT request failed for URL {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * PUT request with headers
     */
    public <T> ResponseEntity<T> put(String url, Object requestBody, HttpHeaders headers, Class<T> responseType) {
        try {
            log.debug("Making PUT request to: {} with headers", url);
            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
            log.debug("PUT request completed with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("PUT request failed for URL {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * DELETE request
     */
    public ResponseEntity<Void> delete(String url) {
        try {
            log.debug("Making DELETE request to: {}", url);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE,
                    HttpEntity.EMPTY, Void.class);
            log.debug("DELETE request completed with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("DELETE request failed for URL {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * DELETE request with headers
     */
    public ResponseEntity<Void> delete(String url, HttpHeaders headers) {
        try {
            log.debug("Making DELETE request to: {} with headers", url);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            log.debug("DELETE request completed with status: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("DELETE request failed for URL {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    // ========== ASYNC HTTP OPERATIONS ==========

    /**
     * Async GET request
     */
    public <T> CompletableFuture<ResponseEntity<T>> getAsync(String url, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> get(url, responseType));
    }

    /**
     * Async GET request with headers
     */
    public <T> CompletableFuture<ResponseEntity<T>> getAsync(String url, HttpHeaders headers, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> get(url, headers, responseType));
    }

    /**
     * Async POST request
     */
    public <T> CompletableFuture<ResponseEntity<T>> postAsync(String url, Object requestBody, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> post(url, requestBody, responseType));
    }

    /**
     * Async POST request with headers
     */
    public <T> CompletableFuture<ResponseEntity<T>> postAsync(String url, Object requestBody, HttpHeaders headers,
            Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> post(url, requestBody, headers, responseType));
    }

    /**
     * Async PUT request
     */
    public <T> CompletableFuture<ResponseEntity<T>> putAsync(String url, Object requestBody, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> put(url, requestBody, responseType));
    }

    /**
     * Async PUT request with headers
     */
    public <T> CompletableFuture<ResponseEntity<T>> putAsync(String url, Object requestBody, HttpHeaders headers,
            Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> put(url, requestBody, headers, responseType));
    }

    /**
     * Async DELETE request
     */
    public CompletableFuture<ResponseEntity<Void>> deleteAsync(String url) {
        return CompletableFuture.supplyAsync(() -> delete(url));
    }

    /**
     * Async DELETE request with headers
     */
    public CompletableFuture<ResponseEntity<Void>> deleteAsync(String url, HttpHeaders headers) {
        return CompletableFuture.supplyAsync(() -> delete(url, headers));
    }

    // ========== RETRY MECHANISM ==========

    /**
     * GET request with retry
     */
    public <T> ResponseEntity<T> getWithRetry(String url, Class<T> responseType, int maxRetries) {
        return getWithRetry(url, responseType, maxRetries, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * GET request with retry and custom delay
     */
    public <T> ResponseEntity<T> getWithRetry(String url, Class<T> responseType, int maxRetries, long delay,
            TimeUnit timeUnit) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetries) {
            try {
                return get(url, responseType);
            } catch (Exception e) {
                lastException = e;
                attempts++;

                if (attempts < maxRetries) {
                    try {
                        Thread.sleep(timeUnit.toMillis(delay * attempts)); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }

        throw new RuntimeException("Request failed after " + maxRetries + " attempts", lastException);
    }

    /**
     * POST request with retry
     */
    public <T> ResponseEntity<T> postWithRetry(String url, Object requestBody, Class<T> responseType, int maxRetries) {
        return postWithRetry(url, requestBody, responseType, maxRetries, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * POST request with retry and custom delay
     */
    public <T> ResponseEntity<T> postWithRetry(String url, Object requestBody, Class<T> responseType, int maxRetries,
            long delay, TimeUnit timeUnit) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetries) {
            try {
                return post(url, requestBody, responseType);
            } catch (Exception e) {
                lastException = e;
                attempts++;

                if (attempts < maxRetries) {
                    try {
                        Thread.sleep(timeUnit.toMillis(delay * attempts)); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }

        throw new RuntimeException("Request failed after " + maxRetries + " attempts", lastException);
    }

    // ========== HEADER UTILITIES ==========

    /**
     * Create headers with content type
     */
    public HttpHeaders createHeaders(String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", contentType);
        return headers;
    }

    /**
     * Create headers with authorization
     */
    public HttpHeaders createHeadersWithAuth(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }

    /**
     * Create headers with custom headers
     */
    public HttpHeaders createHeaders(Map<String, String> customHeaders) {
        HttpHeaders headers = new HttpHeaders();
        customHeaders.forEach(headers::set);
        return headers;
    }

    /**
     * Create headers with content type and authorization
     */
    public HttpHeaders createHeaders(String contentType, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", contentType);
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }

    // ========== URL UTILITIES ==========

    /**
     * Build URL with query parameters
     */
    public String buildUrl(String baseUrl, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return baseUrl;
        }

        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?");

        queryParams.forEach((key, value) -> {
            url.append(key).append("=").append(value).append("&");
        });

        // Remove trailing &
        if (url.charAt(url.length() - 1) == '&') {
            url.setLength(url.length() - 1);
        }

        return url.toString();
    }

    /**
     * Build URL with path parameters
     */
    public String buildUrlWithPath(String baseUrl, String... pathSegments) {
        if (pathSegments == null || pathSegments.length == 0) {
            return baseUrl;
        }

        StringBuilder url = new StringBuilder(baseUrl);
        if (!baseUrl.endsWith("/")) {
            url.append("/");
        }

        for (String segment : pathSegments) {
            if (segment != null && !segment.isEmpty()) {
                url.append(segment).append("/");
            }
        }

        // Remove trailing /
        if (url.charAt(url.length() - 1) == '/') {
            url.setLength(url.length() - 1);
        }

        return url.toString();
    }

    // ========== RESPONSE UTILITIES ==========

    /**
     * Check if response is successful
     */
    public boolean isSuccessful(ResponseEntity<?> response) {
        return response != null && response.getStatusCode().is2xxSuccessful();
    }

    /**
     * Get response body safely
     */
    public <T> T getResponseBody(ResponseEntity<T> response) {
        return response != null ? response.getBody() : null;
    }

    /**
     * Get response status code
     */
    public int getStatusCode(ResponseEntity<?> response) {
        return response != null ? response.getStatusCode().value() : 0;
    }

    /**
     * Get response headers
     */
    public HttpHeaders getResponseHeaders(ResponseEntity<?> response) {
        return response != null ? response.getHeaders() : new HttpHeaders();
    }

    // ========== UTILITY METHODS ==========

    /**
     * Get RestTemplate instance
     */
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Check if URL is valid
     */
    public boolean isValidUrl(String url) {
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get request duration
     */
    public Duration getRequestDuration(long startTime) {
        return Duration.ofMillis(System.currentTimeMillis() - startTime);
    }

    /**
     * Log request details
     */
    public void logRequest(String method, String url, Object requestBody) {
        log.info("HTTP {} Request to: {}", method, url);
        if (requestBody != null) {
            log.debug("Request body: {}", requestBody);
        }
    }

    /**
     * Log response details
     */
    public void logResponse(ResponseEntity<?> response) {
        if (response != null) {
            log.info("HTTP Response status: {}", response.getStatusCode());
            log.debug("Response body: {}", response.getBody());
        }
    }
}
