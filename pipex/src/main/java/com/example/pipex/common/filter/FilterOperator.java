package com.example.pipex.common.filter;

/**
 * Filter operators enum với enterprise-grade features
 * - Support operators: EQ, NE, GT, GTE, LT, LTE, LIKE, IN, BETWEEN, IS_NULL,
 * IS_NOT_NULL
 * - Each operator has validation methods
 */
public enum FilterOperator {

    // ========== EQUALITY OPERATORS ==========

    EQ("equals", true, false),
    NE("not equals", true, false),

    // ========== COMPARISON OPERATORS ==========

    GT("greater than", true, false),
    GTE("greater than or equal", true, false),
    LT("less than", true, false),
    LTE("less than or equal", true, false),

    // ========== STRING OPERATORS ==========

    LIKE("like", true, false),

    // ========== COLLECTION OPERATORS ==========

    IN("in", false, true),

    // ========== RANGE OPERATORS ==========

    BETWEEN("between", false, true),

    // ========== NULL OPERATORS ==========

    IS_NULL("is null", false, false),
    IS_NOT_NULL("is not null", false, false);

    private final String description;
    private final boolean requiresValue;
    private final boolean requiresValues;

    FilterOperator(String description, boolean requiresValue, boolean requiresValues) {
        this.description = description;
        this.requiresValue = requiresValue;
        this.requiresValues = requiresValues;
    }

    // ========== GETTERS ==========

    public String getDescription() {
        return description;
    }

    public boolean requiresValue() {
        return requiresValue;
    }

    public boolean requiresValues() {
        return requiresValues;
    }

    // ========== VALIDATION ==========

    public boolean isValid() {
        // All operators are valid by default
        return true;
    }

    public boolean isEqualityOperator() {
        return this == EQ || this == NE;
    }

    public boolean isComparisonOperator() {
        return this == GT || this == GTE || this == LT || this == LTE;
    }

    public boolean isStringOperator() {
        return this == LIKE;
    }

    public boolean isCollectionOperator() {
        return this == IN;
    }

    public boolean isRangeOperator() {
        return this == BETWEEN;
    }

    public boolean isNullOperator() {
        return this == IS_NULL || this == IS_NOT_NULL;
    }

    public boolean isNumericOperator() {
        return isComparisonOperator() || this == BETWEEN;
    }

    // ========== UTILITY METHODS ==========

    public static FilterOperator fromString(String operator) {
        if (operator == null || operator.trim().isEmpty()) {
            throw new IllegalArgumentException("Operator cannot be null or empty");
        }

        try {
            return FilterOperator.valueOf(operator.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid operator: " + operator, e);
        }
    }

    public static boolean isValidOperator(String operator) {
        try {
            fromString(operator);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static FilterOperator[] getEqualityOperators() {
        return new FilterOperator[] { EQ, NE };
    }

    public static FilterOperator[] getComparisonOperators() {
        return new FilterOperator[] { GT, GTE, LT, LTE };
    }

    public static FilterOperator[] getStringOperators() {
        return new FilterOperator[] { LIKE };
    }

    public static FilterOperator[] getCollectionOperators() {
        return new FilterOperator[] { IN };
    }

    public static FilterOperator[] getRangeOperators() {
        return new FilterOperator[] { BETWEEN };
    }

    public static FilterOperator[] getNullOperators() {
        return new FilterOperator[] { IS_NULL, IS_NOT_NULL };
    }

    public static FilterOperator[] getNumericOperators() {
        return new FilterOperator[] { GT, GTE, LT, LTE, BETWEEN };
    }

    // ========== STRING REPRESENTATION ==========

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public String toDisplayString() {
        return description;
    }
}
