package com.example.pipex.common.controller;

import com.example.pipex.common.dto.ApiResponse;
import com.example.pipex.common.repository.GenericRepositoryFactory;
import com.example.pipex.common.service.BaseService;
import com.example.pipex.common.service.GenericServiceFactory;
import com.example.pipex.common.utils.ApiResponseBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.pipex.auth.dto.ChatwootTokenValidationResponse;
import com.example.pipex.auth.dto.ChatwootUserData;
import com.example.pipex.auth.ChatwootAuthenticationToken;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Base controller với ApiResponse wrapper
 * Tự động tạo Repository và Service từ GenericRepositoryFactory
 * 
 * Usage: Chỉ cần extend - tự động inject và detect entity class từ generic type
 * 
 * Example:
 * public class LeadStageController extends BaseController<LeadStage, Long> {
 * // Không cần thêm gì cả! CRUD APIs tự động có sẵn
 * }
 */
public abstract class BaseController<T, ID> {

    @Autowired
    private GenericRepositoryFactory repositoryFactory;

    @Autowired
    private GenericServiceFactory serviceFactory;

    protected BaseService<T, ID> service;

    /**
     * Initialize service after dependency injection
     * Tự động detect entity class từ generic type parameter
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    private void initializeService() {
        Class<T> entityClass = getEntityClassFromGenericType();
        if (entityClass == null) {
            throw new IllegalStateException(
                    "Cannot determine entity class from generic type. " +
                            "Please ensure the controller extends BaseController<EntityClass, IdType> correctly.");
        }

        // Repository factory returns JpaRepository<T, Serializable>
        // Cast safely to JpaRepository<T, ID> since ID extends Serializable
        @SuppressWarnings("unchecked")
        org.springframework.data.jpa.repository.JpaRepository<T, ID> repository = (org.springframework.data.jpa.repository.JpaRepository<T, ID>) (org.springframework.data.jpa.repository.JpaRepository<?, ?>) repositoryFactory
                .createRepository(entityClass);
        this.service = serviceFactory.createService(entityClass, repository);
    }

    /**
     * Get entity class from generic type parameter using reflection
     * Extracts the first type argument from BaseController<T, ID>
     */
    @SuppressWarnings("unchecked")
    private Class<T> getEntityClassFromGenericType() {
        try {
            // Get the generic superclass (BaseController<T, ID>)
            java.lang.reflect.Type superclass = this.getClass().getGenericSuperclass();

            if (superclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) superclass;

                // Get the actual type arguments (T, ID)
                java.lang.reflect.Type[] typeArguments = parameterizedType.getActualTypeArguments();

                if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                    return (Class<T>) typeArguments[0];
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<T>>> findAll() {
        List<T> data = service.findAll();
        return ApiResponseBuilder.success(data, "Retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> findById(@PathVariable ID id) {
        T data = service.findById(id);
        return ApiResponseBuilder.success(data);
    }

    @PostMapping
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<ApiResponse<T>> create(@RequestBody T dto) {
        T data = service.create(dto);
        return ApiResponseBuilder.created(data, "Resource created successfully");
    }

    @PutMapping("/{id}")
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<ApiResponse<T>> update(@PathVariable ID id, @RequestBody T dto) {
        T data = service.update(id, dto);
        return ApiResponseBuilder.success(data, "Resource updated successfully");
    }

    @DeleteMapping("/{id}")
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id) {
        service.delete(id);
        return ApiResponseBuilder.success("Resource deleted successfully");
    }

    /**
     * Get validation result from SecurityContextHolder (set by AuthFilter)
     * Preferred method - uses SecurityContextHolder directly
     */
    protected ChatwootTokenValidationResponse getValidationResult() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof ChatwootAuthenticationToken) {
            ChatwootAuthenticationToken chatwootAuth = (ChatwootAuthenticationToken) authentication;
            return chatwootAuth.getValidationResult();
        }
        return null;
    }

    /**
     * Get validation result from SecurityContextHolder (set by AuthFilter)
     * Falls back to request attribute for backward compatibility
     */
    protected ChatwootTokenValidationResponse getValidationResult(HttpServletRequest request) {
        // Try SecurityContextHolder first
        ChatwootTokenValidationResponse result = getValidationResult();
        if (result != null) {
            return result;
        }

        // Fallback to request attribute
        Object validationResult = request.getAttribute("validationResult");
        if (validationResult instanceof ChatwootTokenValidationResponse) {
            return (ChatwootTokenValidationResponse) validationResult;
        }
        return null;
    }

    /**
     * Get current user from SecurityContextHolder (set by AuthFilter)
     * Preferred method - uses SecurityContextHolder directly
     */
    protected ChatwootUserData getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof ChatwootAuthenticationToken) {
            ChatwootAuthenticationToken chatwootAuth = (ChatwootAuthenticationToken) authentication;
            return chatwootAuth.getPrincipal();
        }
        return null;
    }

    /**
     * Get current user from SecurityContextHolder (set by AuthFilter)
     * Falls back to request attribute for backward compatibility
     */
    protected ChatwootUserData getCurrentUser(HttpServletRequest request) {
        // Try SecurityContextHolder first
        ChatwootUserData user = getCurrentUser();
        if (user != null) {
            return user;
        }

        // Fallback to validation result from request attribute
        ChatwootTokenValidationResponse validationResult = getValidationResult(request);
        if (validationResult != null && validationResult.isValid() && validationResult.getData() != null) {
            return validationResult.getData();
        }

        // Fallback to direct attribute access
        Object userData = request.getAttribute("currentUser");
        if (userData instanceof ChatwootUserData) {
            return (ChatwootUserData) userData;
        }
        return null;
    }

    /**
     * Get account ID from SecurityContextHolder (set by AuthFilter)
     * Preferred method - uses SecurityContextHolder directly
     */
    protected Long getAccountId() {
        ChatwootUserData currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getAccountId() != null) {
            return currentUser.getAccountId().longValue();
        }
        return null;
    }

    /**
     * Get account ID from SecurityContextHolder (set by AuthFilter)
     * Falls back to request attribute for backward compatibility
     */
    protected Long getAccountId(HttpServletRequest request) {
        // Try SecurityContextHolder first
        Long accountId = getAccountId();
        if (accountId != null) {
            return accountId;
        }

        // Fallback to direct attribute access
        Object accountIdAttr = request.getAttribute("accountId");
        if (accountIdAttr instanceof Number) {
            return ((Number) accountIdAttr).longValue();
        }
        return null;
    }
}
