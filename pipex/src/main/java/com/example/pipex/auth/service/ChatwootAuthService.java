package com.example.pipex.auth.service;

import com.example.pipex.auth.dto.ChatwootTokenValidationRequest;
import com.example.pipex.auth.dto.ChatwootTokenValidationResponse;
import com.example.pipex.auth.dto.ChatwootUserData;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ChatwootAuthService {

    @Value("${chatwoot.base-url}")
    private String chatwootBaseUrl;

    private final RestTemplate restTemplate;

    public ChatwootAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Validate Chatwoot token with caching
     * Cache key: accessToken
     * Cache TTL: 10 minutes (configured in CachingConfig)
     * 
     * Only caches successful validations (isValid = true)
     * Failed validations are not cached to allow retry
     */
    @Cacheable(value = "authTokens", condition = "#tokenData != null && #tokenData.accessToken != null && !#tokenData.accessToken.isEmpty()", key = "'chatwoot:auth:' + #tokenData.accessToken", unless = "#result == null || !#result.isValid()")
    public ChatwootTokenValidationResponse validateChatwootToken(ChatwootTokenValidationRequest tokenData) {
        log.debug("Validating Chatwoot token (cache miss)");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            headers.set("access-token", tokenData.getAccessToken());
            headers.set("client", tokenData.getClient());
            headers.set("expiry", tokenData.getExpiry());
            headers.set("token-type", tokenData.getTokenType());
            headers.set("uid", tokenData.getUid());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String validateUrl = chatwootBaseUrl + "/auth/validate_token";
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    validateUrl,
                    HttpMethod.GET,
                    entity,
                    (Class<Map<String, Object>>) (Class<?>) Map.class);

            // Parse response data from API
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("payload")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> payload = (Map<String, Object>) responseBody.get("payload");
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) payload.get("data");

                ChatwootUserData userData = ChatwootUserData.builder()
                        .id((Integer) data.get("id"))
                        .name((String) data.get("name"))
                        .email((String) data.get("email"))
                        .role((String) data.get("role"))
                        .accountId((Integer) data.get("account_id"))
                        .build();

                ChatwootTokenValidationResponse validationResponse = ChatwootTokenValidationResponse.builder()
                        .isValid(true)
                        .data(userData)
                        .build();

                log.debug("Token validation successful for user: {} (account: {})", userData.getId(),
                        userData.getAccountId());
                return validationResponse;
            }

            log.warn("Invalid response format from Chatwoot validation");
            return ChatwootTokenValidationResponse.builder()
                    .isValid(false)
                    .error(Map.of("message", "Invalid response format"))
                    .build();

        } catch (Exception error) {
            log.error("Token validation failed: {}", error.getMessage(), error);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Token validation failed");
            errorResponse.put("status", error.getMessage());

            return ChatwootTokenValidationResponse.builder()
                    .isValid(false)
                    .error(errorResponse)
                    .build();
        }
    }

    /**
     * Validate auth payload from request headers
     * Similar to validateAuth function in chatwoot-api.js
     */
    public ChatwootTokenValidationResponse validateAuth(Map<String, String> authPayload) {
        ChatwootTokenValidationRequest tokenData = ChatwootTokenValidationRequest.builder()
                .accessToken(authPayload.get("access-token"))
                .client(authPayload.get("client"))
                .expiry(authPayload.get("expiry"))
                .tokenType(authPayload.get("token-type"))
                .uid(authPayload.get("uid"))
                .build();

        return validateChatwootToken(tokenData);
    }

    /**
     * Evict cache for specific token
     * Useful when user logs out or token is revoked
     */
    @CacheEvict(value = "authTokens", key = "'chatwoot:auth:' + #accessToken")
    public void evictTokenCache(String accessToken) {
        log.debug("Evicting cache for token: {}", accessToken);
    }

    /**
     * Evict all auth tokens cache
     * Use with caution - only for maintenance scenarios
     */
    @CacheEvict(value = "authTokens", allEntries = true)
    public void evictAllAuthTokensCache() {
        log.warn("Evicting all auth tokens cache - this should be used sparingly");
    }
}
