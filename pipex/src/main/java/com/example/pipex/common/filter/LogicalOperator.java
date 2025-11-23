package com.example.pipex.common.filter;

/**
 * Logical operators for combining filter criteria
 * - AND: All criteria must be satisfied
 * - OR: At least one criterion must be satisfied
 */
public enum LogicalOperator {

    AND("and", "All criteria must be satisfied"),
    OR("or", "At least one criterion must be satisfied");

    private final String symbol;
    private final String description;

    LogicalOperator(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    // ========== GETTERS ==========

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }

    // ========== UTILITY METHODS ==========

    public static LogicalOperator fromString(String operator) {
        if (operator == null || operator.trim().isEmpty()) {
            return AND; // Default to AND
        }

        try {
            return LogicalOperator.valueOf(operator.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid logical operator: " + operator, e);
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

    public static LogicalOperator getDefault() {
        return AND;
    }

    // ========== STRING REPRESENTATION ==========

    @Override
    public String toString() {
        return symbol;
    }

    public String toDisplayString() {
        return description;
    }
}
