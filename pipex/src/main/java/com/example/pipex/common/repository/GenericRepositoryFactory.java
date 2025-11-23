package com.example.pipex.common.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Generic Repository Factory
 * Tự động tạo JpaRepository cho bất kỳ Entity nào
 * Không cần tạo Repository interface riêng
 * 
 * Note: EntityManager injected is already a SharedEntityManager proxy
 * that will automatically use EntityManager from transaction context
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenericRepositoryFactory {

    private final EntityManager entityManager;

    /**
     * Create JpaRepository instance for any Entity class
     * Uses SimpleJpaRepository với JpaEntityInformation
     * EntityManager is SharedEntityManager proxy that uses transaction context
     * automatically
     */
    @SuppressWarnings("unchecked")
    public <T, ID extends Serializable> org.springframework.data.jpa.repository.JpaRepository<T, ID> createRepository(
            Class<T> entityClass) {

        log.debug("Creating repository for entity: {}", entityClass.getSimpleName());

        // Validate entity class
        if (entityManager.getMetamodel().entity(entityClass) == null) {
            throw new IllegalArgumentException("Class " + entityClass.getName() + " is not a JPA entity");
        }

        // Create JpaEntityInformation
        JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(entityClass,
                entityManager);

        // Create SimpleJpaRepository instance
        // EntityManager is SharedEntityManager proxy that will use EntityManager from
        // transaction context
        @SuppressWarnings("unchecked")
        SimpleJpaRepository<T, ID> repository = new SimpleJpaRepository<>(
                (JpaEntityInformation<T, ID>) entityInformation,
                entityManager);

        log.debug("Repository created successfully for: {}", entityClass.getSimpleName());
        return repository;
    }
}
