package com.example.pipex.common.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.*;
import java.util.List;

/**
 * Specification builder với enterprise-grade features
 * - Spring bean converting FilterCriteria to JPA Specification
 * - Dynamic query construction
 * - Type-safe predicate building
 * - Support nested properties (e.g., customer.name)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpecificationBuilder {

    // ========== MAIN BUILD METHOD ==========

    /**
     * Build JPA Specification from FilterRequest
     */
    public <T> Specification<T> build(FilterRequest filterRequest) {
        if (filterRequest == null || !filterRequest.hasCriteria()) {
            return Specification.where((root, query, criteriaBuilder) -> null);
        }

        List<FilterCriteria> criteria = filterRequest.getCriteria();
        LogicalOperator logicalOperator = filterRequest.getLogicalOperator();

        return (root, query, criteriaBuilder) -> {
            if (criteria.size() == 1) {
                return buildPredicate(criteria.get(0), root, criteriaBuilder);
            }

            Predicate[] predicates = criteria.stream()
                    .map(criterion -> buildPredicate(criterion, root, criteriaBuilder))
                    .toArray(Predicate[]::new);

            return logicalOperator == LogicalOperator.AND
                    ? criteriaBuilder.and(predicates)
                    : criteriaBuilder.or(predicates);
        };
    }

    /**
     * Build JPA Specification from single FilterCriteria
     */
    public <T> Specification<T> build(FilterCriteria criterion) {
        if (criterion == null || !criterion.isValid()) {
            return Specification.where((root, query, criteriaBuilder) -> null);
        }

        return (root, query, criteriaBuilder) -> buildPredicate(criterion, root, criteriaBuilder);
    }

    /**
     * Build JPA Specification from list of FilterCriteria
     */
    public <T> Specification<T> build(List<FilterCriteria> criteria, LogicalOperator logicalOperator) {
        if (criteria == null || criteria.isEmpty()) {
            return Specification.where((root, query, criteriaBuilder) -> null);
        }

        return (root, query, criteriaBuilder) -> {
            if (criteria.size() == 1) {
                return buildPredicate(criteria.get(0), root, criteriaBuilder);
            }

            Predicate[] predicates = criteria.stream()
                    .map(criterion -> buildPredicate(criterion, root, criteriaBuilder))
                    .toArray(Predicate[]::new);

            return logicalOperator == LogicalOperator.AND
                    ? criteriaBuilder.and(predicates)
                    : criteriaBuilder.or(predicates);
        };
    }

    // ========== PREDICATE BUILDING ==========

    /**
     * Build predicate from FilterCriteria
     */
    private <T> Predicate buildPredicate(FilterCriteria criterion, Root<T> root, CriteriaBuilder criteriaBuilder) {
        try {
            Path<?> path = getPath(root, criterion.getField());
            FilterOperator operator = criterion.getOperator();

            return switch (operator) {
                case EQ -> criteriaBuilder.equal(path, criterion.getValue());
                case NE -> criteriaBuilder.notEqual(path, criterion.getValue());
                case GT -> criteriaBuilder.greaterThan((Path<Comparable>) path, (Comparable) criterion.getValue());
                case GTE ->
                    criteriaBuilder.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) criterion.getValue());
                case LT -> criteriaBuilder.lessThan((Path<Comparable>) path, (Comparable) criterion.getValue());
                case LTE ->
                    criteriaBuilder.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) criterion.getValue());
                case LIKE -> criteriaBuilder.like((Path<String>) path, "%" + criterion.getValue() + "%");
                case IN -> path.in(criterion.getValues());
                case BETWEEN -> criteriaBuilder.between((Path<Comparable>) path,
                        (Comparable) criterion.getStartValue(), (Comparable) criterion.getEndValue());
                case IS_NULL -> criteriaBuilder.isNull(path);
                case IS_NOT_NULL -> criteriaBuilder.isNotNull(path);
            };
        } catch (Exception e) {
            log.error("Failed to build predicate for criterion {}: {}", criterion, e.getMessage(), e);
            return criteriaBuilder.conjunction(); // Return true predicate
        }
    }

    // ========== PATH RESOLUTION ==========

    /**
     * Get path for field, supporting nested properties
     */
    private <T> Path<?> getPath(Root<T> root, String field) {
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalArgumentException("Field cannot be null or empty");
        }

        String[] fieldParts = field.split("\\.");
        Path<?> path = root;

        for (String part : fieldParts) {
            if (part.trim().isEmpty()) {
                throw new IllegalArgumentException("Field part cannot be empty");
            }
            path = path.get(part.trim());
        }

        return path;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Check if field is nested (contains dots)
     */
    public boolean isNestedField(String field) {
        return field != null && field.contains(".");
    }

    /**
     * Get root field name from nested field
     */
    public String getRootField(String field) {
        if (field == null || !field.contains(".")) {
            return field;
        }
        return field.split("\\.")[0];
    }

    /**
     * Get nested field path from nested field
     */
    public String[] getNestedPath(String field) {
        if (field == null || !field.contains(".")) {
            return new String[] { field };
        }
        return field.split("\\.");
    }

    /**
     * Validate field name
     */
    public boolean isValidFieldName(String field) {
        if (field == null || field.trim().isEmpty()) {
            return false;
        }

        // Check for valid field name pattern
        return field.matches("^[a-zA-Z_][a-zA-Z0-9_.]*$");
    }

    // ========== FACTORY METHODS ==========

    /**
     * Create specification for equality
     */
    public <T> Specification<T> eq(String field, Object value) {
        return build(FilterCriteria.eq(field, value));
    }

    /**
     * Create specification for not equality
     */
    public <T> Specification<T> ne(String field, Object value) {
        return build(FilterCriteria.ne(field, value));
    }

    /**
     * Create specification for greater than
     */
    public <T> Specification<T> gt(String field, Object value) {
        return build(FilterCriteria.gt(field, value));
    }

    /**
     * Create specification for greater than or equal
     */
    public <T> Specification<T> gte(String field, Object value) {
        return build(FilterCriteria.gte(field, value));
    }

    /**
     * Create specification for less than
     */
    public <T> Specification<T> lt(String field, Object value) {
        return build(FilterCriteria.lt(field, value));
    }

    /**
     * Create specification for less than or equal
     */
    public <T> Specification<T> lte(String field, Object value) {
        return build(FilterCriteria.lte(field, value));
    }

    /**
     * Create specification for like
     */
    public <T> Specification<T> like(String field, Object value) {
        return build(FilterCriteria.like(field, value));
    }

    /**
     * Create specification for in
     */
    public <T> Specification<T> in(String field, List<Object> values) {
        return build(FilterCriteria.in(field, values));
    }

    /**
     * Create specification for between
     */
    public <T> Specification<T> between(String field, Object startValue, Object endValue) {
        return build(FilterCriteria.between(field, startValue, endValue));
    }

    /**
     * Create specification for is null
     */
    public <T> Specification<T> isNull(String field) {
        return build(FilterCriteria.isNull(field));
    }

    /**
     * Create specification for is not null
     */
    public <T> Specification<T> isNotNull(String field) {
        return build(FilterCriteria.isNotNull(field));
    }

    // ========== COMBINATION METHODS ==========

    /**
     * Combine specifications with AND
     */
    public <T> Specification<T> and(Specification<T> spec1, Specification<T> spec2) {
        return Specification.where(spec1).and(spec2);
    }

    /**
     * Combine specifications with OR
     */
    public <T> Specification<T> or(Specification<T> spec1, Specification<T> spec2) {
        return Specification.where(spec1).or(spec2);
    }

    /**
     * Combine multiple specifications with AND
     */
    @SafeVarargs
    public final <T> Specification<T> and(Specification<T>... specifications) {
        if (specifications == null || specifications.length == 0) {
            return Specification.where((root, query, criteriaBuilder) -> null);
        }

        Specification<T> result = specifications[0];
        for (int i = 1; i < specifications.length; i++) {
            result = result.and(specifications[i]);
        }
        return result;
    }

    /**
     * Combine multiple specifications with OR
     */
    @SafeVarargs
    public final <T> Specification<T> or(Specification<T>... specifications) {
        if (specifications == null || specifications.length == 0) {
            return Specification.where((root, query, criteriaBuilder) -> null);
        }

        Specification<T> result = specifications[0];
        for (int i = 1; i < specifications.length; i++) {
            result = result.or(specifications[i]);
        }
        return result;
    }
}
