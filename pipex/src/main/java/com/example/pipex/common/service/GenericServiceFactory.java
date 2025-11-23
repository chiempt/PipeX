package com.example.pipex.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generic Service Factory
 * Creates BaseService instances for any Entity class
 * Transaction is managed at controller level via @Transactional annotation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenericServiceFactory {

    /**
     * Create BaseService instance for any Entity class
     * Transaction is managed at controller level via @Transactional annotation
     */
    @SuppressWarnings("unchecked")
    public <T, ID> BaseService<T, ID> createService(
            Class<T> entityClass,
            org.springframework.data.jpa.repository.JpaRepository<T, ID> repository) {

        log.debug("Creating service for entity: {}", entityClass.getSimpleName());

        BaseServiceImpl<T, ID> service = new BaseServiceImpl<>(repository);
        
        log.debug("Service created successfully for: {}", entityClass.getSimpleName());
        return service;
    }
}

