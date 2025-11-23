package com.example.pipex.common.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filter criteria for advanced API filtering
 * - Support operators: EQ, NE, GT, GTE, LT, LTE, LIKE, IN, BETWEEN, IS_NULL,
 * IS_NOT_NULL
 * - Field name, operator, value(s)
 * - Logical operators: AND, OR
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriteria {

    private String field;
    private FilterOperator operator;
    private Object value;
    private List<Object> values; // For IN and BETWEEN operations
    private LogicalOperator logicalOperator = LogicalOperator.AND;

    // ========== CONSTRUCTORS ==========

    public FilterCriteria(String field, FilterOperator operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public FilterCriteria(String field, FilterOperator operator, List<Object> values) {
        this.field = field;
        this.operator = operator;
        this.values = values;
    }

    public FilterCriteria(String field, FilterOperator operator, Object value, LogicalOperator logicalOperator) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.logicalOperator = logicalOperator;
    }

    // ========== FACTORY METHODS ==========

    public static FilterCriteria eq(String field, Object value) {
        return new FilterCriteria(field, FilterOperator.EQ, value);
    }

    public static FilterCriteria ne(String field, Object value) {
        return new FilterCriteria(field, FilterOperator.NE, value);
    }

    public static FilterCriteria gt(String field, Object value) {
        return new FilterCriteria(field, FilterOperator.GT, value);
    }

    public static FilterCriteria gte(String field, Object value) {
        return new FilterCriteria(field, FilterOperator.GTE, value);
    }

    public static FilterCriteria lt(String field, Object value) {
        return new FilterCriteria(field, FilterOperator.LT, value);
    }

    public static FilterCriteria lte(String field, Object value) {
        return new FilterCriteria(field, FilterOperator.LTE, value);
    }

    public static FilterCriteria like(String field, Object value) {
        return new FilterCriteria(field, FilterOperator.LIKE, value);
    }

    public static FilterCriteria in(String field, List<Object> values) {
        return new FilterCriteria(field, FilterOperator.IN, values);
    }

    public static FilterCriteria between(String field, Object startValue, Object endValue) {
        return new FilterCriteria(field, FilterOperator.BETWEEN, List.of(startValue, endValue));
    }

    public static FilterCriteria isNull(String field) {
        return new FilterCriteria(field, FilterOperator.IS_NULL, null);
    }

    public static FilterCriteria isNotNull(String field) {
        return new FilterCriteria(field, FilterOperator.IS_NOT_NULL, null);
    }

    // ========== LOGICAL OPERATOR METHODS ==========

    public FilterCriteria and() {
        this.logicalOperator = LogicalOperator.AND;
        return this;
    }

    public FilterCriteria or() {
        this.logicalOperator = LogicalOperator.OR;
        return this;
    }

    // ========== VALIDATION ==========

    public boolean isValid() {
        if (field == null || field.trim().isEmpty()) {
            return false;
        }

        if (operator == null) {
            return false;
        }

        // Check if operator requires value
        if (operator.requiresValue() && value == null && (values == null || values.isEmpty())) {
            return false;
        }

        // Check if operator requires values list
        if (operator.requiresValues() && (values == null || values.isEmpty())) {
            return false;
        }

        return true;
    }

    // ========== UTILITY METHODS ==========

    public boolean isNullCheck() {
        return operator == FilterOperator.IS_NULL || operator == FilterOperator.IS_NOT_NULL;
    }

    public boolean isRangeCheck() {
        return operator == FilterOperator.BETWEEN;
    }

    public boolean isListCheck() {
        return operator == FilterOperator.IN;
    }

    public boolean isStringCheck() {
        return operator == FilterOperator.LIKE;
    }

    public boolean isNumericCheck() {
        return operator == FilterOperator.GT || operator == FilterOperator.GTE ||
                operator == FilterOperator.LT || operator == FilterOperator.LTE;
    }

    public boolean isEqualityCheck() {
        return operator == FilterOperator.EQ || operator == FilterOperator.NE;
    }

    // ========== GETTERS WITH VALIDATION ==========

    public Object getValue() {
        if (operator.requiresValue() && value == null) {
            throw new IllegalStateException("FilterCriteria requires value for operator: " + operator);
        }
        return value;
    }

    public List<Object> getValues() {
        if (operator.requiresValues() && (values == null || values.isEmpty())) {
            throw new IllegalStateException("FilterCriteria requires values list for operator: " + operator);
        }
        return values;
    }

    public Object getStartValue() {
        if (operator == FilterOperator.BETWEEN && values != null && values.size() >= 2) {
            return values.get(0);
        }
        throw new IllegalStateException("FilterCriteria requires two values for BETWEEN operator");
    }

    public Object getEndValue() {
        if (operator == FilterOperator.BETWEEN && values != null && values.size() >= 2) {
            return values.get(1);
        }
        throw new IllegalStateException("FilterCriteria requires two values for BETWEEN operator");
    }

    // ========== STRING REPRESENTATION ==========

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(field).append(" ").append(operator);

        if (operator.requiresValue()) {
            sb.append(" ").append(value);
        } else if (operator.requiresValues()) {
            sb.append(" ").append(values);
        }

        return sb.toString();
    }
}
