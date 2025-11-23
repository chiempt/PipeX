package com.example.pipex.common.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Generic filter service với enterprise-grade features
 * - Spring bean with generic type support
 * - Apply filters to any JpaRepository
 * - Return paginated results
 * - Cache filter results with Redis
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenericFilterService {

    private final SpecificationBuilder specificationBuilder;

    // ========== BASIC FILTERING ==========

    /**
     * Filter entities with FilterRequest
     */
    public <T> Page<T> filter(JpaSpecificationExecutor<T> repository, FilterRequest filterRequest) {
        if (filterRequest == null || !filterRequest.hasCriteria()) {
            return Page.empty();
        }

        try {

            Specification<T> specification = specificationBuilder.build(filterRequest);
            Pageable pageable = createPageable(filterRequest);

            Page<T> result = repository.findAll(specification, pageable);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Filter operation failed", e);
        }
    }

    /**
     * Filter entities with FilterRequest (cached)
     */
    @Cacheable(value = "filterResults", key = "#filterRequest.toString() + '_' + #repository.getClass().getSimpleName()")
    public <T> Page<T> filterCached(JpaSpecificationExecutor<T> repository, FilterRequest filterRequest) {
        return filter(repository, filterRequest);
    }

    /**
     * Filter entities with single FilterCriteria
     */
    public <T> List<T> filter(JpaSpecificationExecutor<T> repository, FilterCriteria criterion) {
        if (criterion == null || !criterion.isValid()) {
            return List.of();
        }

        try {
            Specification<T> specification = specificationBuilder.build(criterion);
            List<T> result = repository.findAll(specification);

            return result;
        } catch (Exception e) {
            log.error("Filter operation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Filter operation failed", e);
        }
    }

    /**
     * Filter entities with multiple FilterCriteria
     */
    public <T> List<T> filter(JpaSpecificationExecutor<T> repository, List<FilterCriteria> criteria,
            LogicalOperator logicalOperator) {
        if (criteria == null || criteria.isEmpty()) {
            return List.of();
        }

        try {

            Specification<T> specification = specificationBuilder.build(criteria, logicalOperator);
            List<T> result = repository.findAll(specification);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Filter operation failed", e);
        }
    }

    // ========== COUNT OPERATIONS ==========

    /**
     * Count entities matching FilterRequest
     */
    public <T> long count(JpaSpecificationExecutor<T> repository, FilterRequest filterRequest) {
        if (filterRequest == null || !filterRequest.hasCriteria()) {
            return 0;
        }

        try {
            log.debug("Counting entities with {} criteria", filterRequest.getCriteriaCount());

            Specification<T> specification = specificationBuilder.build(filterRequest);
            long count = repository.count(specification);

            log.debug("Count completed: {} entities found", count);
            return count;
        } catch (Exception e) {
            log.error("Count operation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Count operation failed", e);
        }
    }

    /**
     * Count entities matching FilterCriteria
     */
    public <T> long count(JpaSpecificationExecutor<T> repository, FilterCriteria criterion) {
        if (criterion == null || !criterion.isValid()) {
            log.warn("Filter criterion is null or invalid");
            return 0;
        }

        try {
            log.debug("Counting entities with criterion: {}", criterion);

            Specification<T> specification = specificationBuilder.build(criterion);
            long count = repository.count(specification);

            log.debug("Count completed: {} entities found", count);
            return count;
        } catch (Exception e) {
            log.error("Count operation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Count operation failed", e);
        }
    }

    // ========== EXISTS OPERATIONS ==========

    /**
     * Check if entities exist matching FilterRequest
     */
    public <T> boolean exists(JpaSpecificationExecutor<T> repository, FilterRequest filterRequest) {
        return count(repository, filterRequest) > 0;
    }

    /**
     * Check if entities exist matching FilterCriteria
     */
    public <T> boolean exists(JpaSpecificationExecutor<T> repository, FilterCriteria criterion) {
        return count(repository, criterion) > 0;
    }

    // ========== FIND ONE OPERATIONS ==========

    /**
     * Find one entity matching FilterRequest
     */
    public <T> Optional<T> findOne(JpaSpecificationExecutor<T> repository, FilterRequest filterRequest) {
        if (filterRequest == null || !filterRequest.hasCriteria()) {
            log.warn("Filter request is null or has no criteria");
            return Optional.empty();
        }

        try {
            log.debug("Finding one entity with {} criteria", filterRequest.getCriteriaCount());

            Specification<T> specification = specificationBuilder.build(filterRequest);
            Optional<T> result = repository.findOne(specification);

            log.debug("Find one completed: {}", result.isPresent() ? "entity found" : "no entity found");
            return result;
        } catch (Exception e) {
            log.error("Find one operation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Find one operation failed", e);
        }
    }

    /**
     * Find one entity matching FilterCriteria
     */
    public <T> Optional<T> findOne(JpaSpecificationExecutor<T> repository, FilterCriteria criterion) {
        if (criterion == null || !criterion.isValid()) {
            log.warn("Filter criterion is null or invalid");
            return Optional.empty();
        }

        try {
            log.debug("Finding one entity with criterion: {}", criterion);

            Specification<T> specification = specificationBuilder.build(criterion);
            Optional<T> result = repository.findOne(specification);

            log.debug("Find one completed: {}", result.isPresent() ? "entity found" : "no entity found");
            return result;
        } catch (Exception e) {
            log.error("Find one operation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Find one operation failed", e);
        }
    }

    // ========== UTILITY METHODS ==========

    /**
     * Create Pageable from FilterRequest
     */
    private Pageable createPageable(FilterRequest filterRequest) {
        if (!filterRequest.hasPagination()) {
            return Pageable.unpaged();
        }

        int page = filterRequest.getPage();
        int size = filterRequest.getSize();

        if (filterRequest.hasSorting()) {
            Sort.Direction direction = filterRequest.getSortDirection() == FilterRequest.SortDirection.ASC
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, filterRequest.getSortBy());
            return PageRequest.of(page, size, sort);
        }

        return PageRequest.of(page, size);
    }

    /**
     * Validate FilterRequest
     */
    public boolean isValidFilterRequest(FilterRequest filterRequest) {
        if (filterRequest == null) {
            return false;
        }

        if (!filterRequest.hasCriteria()) {
            return false;
        }

        for (FilterCriteria criterion : filterRequest.getCriteria()) {
            if (!criterion.isValid()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validate FilterCriteria
     */
    public boolean isValidFilterCriteria(FilterCriteria criterion) {
        return criterion != null && criterion.isValid();
    }

    // ========== FACTORY METHODS ==========

    /**
     * Create FilterRequest with single criterion
     */
    public FilterRequest createFilterRequest(FilterCriteria criterion) {
        return new FilterRequest(List.of(criterion));
    }

    /**
     * Create FilterRequest with multiple criteria
     */
    public FilterRequest createFilterRequest(List<FilterCriteria> criteria, LogicalOperator logicalOperator) {
        return new FilterRequest(criteria, logicalOperator);
    }

    /**
     * Create FilterRequest with pagination
     */
    public FilterRequest createFilterRequest(List<FilterCriteria> criteria, int page, int size) {
        return new FilterRequest(criteria, page, size);
    }

    /**
     * Create FilterRequest with sorting
     */
    public FilterRequest createFilterRequest(List<FilterCriteria> criteria, String sortBy,
            FilterRequest.SortDirection sortDirection) {
        return new FilterRequest(criteria, 0, 20, sortBy, sortDirection);
    }

    // ========== CONVENIENCE METHODS ==========

    /**
     * Filter by field equals value
     */
    public <T> List<T> filterByFieldEquals(JpaSpecificationExecutor<T> repository, String field, Object value) {
        return filter(repository, FilterCriteria.eq(field, value));
    }

    /**
     * Filter by field like value
     */
    public <T> List<T> filterByFieldLike(JpaSpecificationExecutor<T> repository, String field, Object value) {
        return filter(repository, FilterCriteria.like(field, value));
    }

    /**
     * Filter by field in values
     */
    public <T> List<T> filterByFieldIn(JpaSpecificationExecutor<T> repository, String field, List<Object> values) {
        return filter(repository, FilterCriteria.in(field, values));
    }

    /**
     * Filter by field between values
     */
    public <T> List<T> filterByFieldBetween(JpaSpecificationExecutor<T> repository, String field, Object startValue,
            Object endValue) {
        return filter(repository, FilterCriteria.between(field, startValue, endValue));
    }

    /**
     * Filter by field is null
     */
    public <T> List<T> filterByFieldIsNull(JpaSpecificationExecutor<T> repository, String field) {
        return filter(repository, FilterCriteria.isNull(field));
    }

    /**
     * Filter by field is not null
     */
    public <T> List<T> filterByFieldIsNotNull(JpaSpecificationExecutor<T> repository, String field) {
        return filter(repository, FilterCriteria.isNotNull(field));
    }
}
