package com.example.pipex.common.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filter request DTO với enterprise-grade features
 * - Contains list of FilterCriteria
 * - Pagination parameters (page, size, sort)
 * - Logical operator for combining criteria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {

    private List<FilterCriteria> criteria;
    private LogicalOperator logicalOperator = LogicalOperator.AND;

    // ========== PAGINATION ==========

    private int page = 0;
    private int size = 20;
    private int maxSize = 1000;

    // ========== SORTING ==========

    private String sortBy;
    private SortDirection sortDirection = SortDirection.ASC;

    // ========== CONSTRUCTORS ==========

    public FilterRequest(List<FilterCriteria> criteria) {
        this.criteria = criteria;
    }

    public FilterRequest(List<FilterCriteria> criteria, LogicalOperator logicalOperator) {
        this.criteria = criteria;
        this.logicalOperator = logicalOperator;
    }

    public FilterRequest(List<FilterCriteria> criteria, int page, int size) {
        this.criteria = criteria;
        this.page = page;
        this.size = size;
    }

    public FilterRequest(List<FilterCriteria> criteria, int page, int size, String sortBy,
            SortDirection sortDirection) {
        this.criteria = criteria;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    // ========== VALIDATION ==========

    public boolean isValid() {
        // Check criteria
        if (criteria == null || criteria.isEmpty()) {
            return false;
        }

        // Validate each criterion
        for (FilterCriteria criterion : criteria) {
            if (!criterion.isValid()) {
                return false;
            }
        }

        // Check pagination
        if (page < 0) {
            return false;
        }

        if (size <= 0 || size > maxSize) {
            return false;
        }

        return true;
    }

    // ========== UTILITY METHODS ==========

    public boolean hasCriteria() {
        return criteria != null && !criteria.isEmpty();
    }

    public int getCriteriaCount() {
        return criteria != null ? criteria.size() : 0;
    }

    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    public boolean hasPagination() {
        return size > 0;
    }

    public int getOffset() {
        return page * size;
    }

    public int getLimit() {
        return size;
    }

    // ========== BUILDER METHODS ==========

    public FilterRequest addCriteria(FilterCriteria criterion) {
        if (criteria == null) {
            criteria = new java.util.ArrayList<>();
        }
        criteria.add(criterion);
        return this;
    }

    public FilterRequest addCriteria(String field, FilterOperator operator, Object value) {
        return addCriteria(new FilterCriteria(field, operator, value));
    }

    public FilterRequest addCriteria(String field, FilterOperator operator, List<Object> values) {
        return addCriteria(new FilterCriteria(field, operator, values));
    }

    public FilterRequest setLogicalOperator(LogicalOperator operator) {
        this.logicalOperator = operator;
        return this;
    }

    public FilterRequest setPagination(int page, int size) {
        this.page = page;
        this.size = size;
        return this;
    }

    public FilterRequest setSorting(String sortBy, SortDirection sortDirection) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        return this;
    }

    public FilterRequest setSorting(String sortBy) {
        return setSorting(sortBy, SortDirection.ASC);
    }

    // ========== FACTORY METHODS ==========

    public static FilterRequest of(FilterCriteria... criteria) {
        return new FilterRequest(List.of(criteria));
    }

    public static FilterRequest of(List<FilterCriteria> criteria) {
        return new FilterRequest(criteria);
    }

    public static FilterRequest of(List<FilterCriteria> criteria, LogicalOperator logicalOperator) {
        return new FilterRequest(criteria, logicalOperator);
    }

    public static FilterRequest withPagination(List<FilterCriteria> criteria, int page, int size) {
        return new FilterRequest(criteria, page, size);
    }

    public static FilterRequest withSorting(List<FilterCriteria> criteria, String sortBy, SortDirection sortDirection) {
        return new FilterRequest(criteria, 0, 20, sortBy, sortDirection);
    }

    public static FilterRequest withSorting(List<FilterCriteria> criteria, String sortBy) {
        return withSorting(criteria, sortBy, SortDirection.ASC);
    }

    // ========== SORT DIRECTION ENUM ==========

    public enum SortDirection {
        ASC("asc", "Ascending"),
        DESC("desc", "Descending");

        private final String value;
        private final String description;

        SortDirection(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static SortDirection fromString(String direction) {
            if (direction == null || direction.trim().isEmpty()) {
                return ASC; // Default to ASC
            }

            try {
                return SortDirection.valueOf(direction.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid sort direction: " + direction, e);
            }
        }

        public static boolean isValidDirection(String direction) {
            try {
                fromString(direction);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        @Override
        public String toString() {
            return value;
        }
    }

    // ========== STRING REPRESENTATION ==========

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FilterRequest{");

        if (hasCriteria()) {
            sb.append("criteria=").append(criteria.size()).append(" items");
        }

        if (hasPagination()) {
            sb.append(", page=").append(page).append(", size=").append(size);
        }

        if (hasSorting()) {
            sb.append(", sortBy=").append(sortBy).append(", sortDirection=").append(sortDirection);
        }

        sb.append(", logicalOperator=").append(logicalOperator);
        sb.append("}");

        return sb.toString();
    }
}
