package com.example.pipex.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * String manipulation utilities với enterprise-grade features
 * - Null-safe operations
 * - Sanitization và validation
 * - Case conversion và trimming
 * - Pattern matching utilities
 */
@Slf4j
public final class StringUtils {

    private StringUtils() {
        // Utility class
    }

    // ========== NULL SAFETY ==========

    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Check if string is null or empty or whitespace only
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Check if string is not null and not blank
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Return safe string (empty if null)
     */
    public static String safe(String str) {
        return str != null ? str : "";
    }

    /**
     * Return safe string with default value
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * Return safe string with default value
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }

    // ========== STRING MANIPULATION ==========

    /**
     * Trim string safely
     */
    public static String trim(String str) {
        return str != null ? str.trim() : "";
    }

    /**
     * Trim string safely with null check
     */
    public static String trimToNull(String str) {
        if (str == null)
            return null;
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Trim string safely with empty check
     */
    public static String trimToEmpty(String str) {
        return str != null ? str.trim() : "";
    }

    /**
     * Left pad string
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null)
            str = "";
        if (str.length() >= size)
            return str;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - str.length(); i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * Right pad string
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null)
            str = "";
        if (str.length() >= size)
            return str;

        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < size - str.length(); i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * Center pad string
     */
    public static String centerPad(String str, int size, char padChar) {
        if (str == null)
            str = "";
        if (str.length() >= size)
            return str;

        int totalPadding = size - str.length();
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        for (int i = 0; i < rightPadding; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    // ========== CASE CONVERSION ==========

    /**
     * Convert to uppercase safely
     */
    public static String toUpperCase(String str) {
        return str != null ? str.toUpperCase() : "";
    }

    /**
     * Convert to lowercase safely
     */
    public static String toLowerCase(String str) {
        return str != null ? str.toLowerCase() : "";
    }

    /**
     * Convert to camelCase
     */
    public static String toCamelCase(String str) {
        if (isBlank(str))
            return str;

        String[] words = str.split("[\\s_-]+");
        if (words.length == 0)
            return str;

        StringBuilder result = new StringBuilder(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                result.append(capitalize(words[i].toLowerCase()));
            }
        }
        return result.toString();
    }

    /**
     * Convert to PascalCase
     */
    public static String toPascalCase(String str) {
        if (isBlank(str))
            return str;

        String[] words = str.split("[\\s_-]+");
        if (words.length == 0)
            return str;

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(capitalize(word.toLowerCase()));
            }
        }
        return result.toString();
    }

    /**
     * Convert to snake_case
     */
    public static String toSnakeCase(String str) {
        if (isBlank(str))
            return str;

        return str.replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("[\\s-]+", "_")
                .toLowerCase();
    }

    /**
     * Convert to kebab-case
     */
    public static String toKebabCase(String str) {
        if (isBlank(str))
            return str;

        return str.replaceAll("([a-z])([A-Z])", "$1-$2")
                .replaceAll("[\\s_]+", "-")
                .toLowerCase();
    }

    /**
     * Capitalize first character
     */
    public static String capitalize(String str) {
        if (isBlank(str))
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Uncapitalize first character
     */
    public static String uncapitalize(String str) {
        if (isBlank(str))
            return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    // ========== STRING VALIDATION ==========

    /**
     * Check if string contains only digits
     */
    public static boolean isNumeric(String str) {
        if (isBlank(str))
            return false;
        return str.matches("\\d+");
    }

    /**
     * Check if string contains only letters
     */
    public static boolean isAlpha(String str) {
        if (isBlank(str))
            return false;
        return str.matches("[a-zA-Z]+");
    }

    /**
     * Check if string contains only letters and digits
     */
    public static boolean isAlphanumeric(String str) {
        if (isBlank(str))
            return false;
        return str.matches("[a-zA-Z0-9]+");
    }

    /**
     * Check if string is valid email
     */
    public static boolean isValidEmail(String email) {
        if (isBlank(email))
            return false;
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * Check if string is valid phone number
     */
    public static boolean isValidPhone(String phone) {
        if (isBlank(phone))
            return false;
        String phoneRegex = "^[+]?[0-9\\s\\-\\(\\)]{7,20}$";
        return Pattern.matches(phoneRegex, phone);
    }

    /**
     * Check if string is valid URL
     */
    public static boolean isValidUrl(String url) {
        if (isBlank(url))
            return false;
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if string matches pattern
     */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null)
            return false;
        return Pattern.matches(regex, str);
    }

    // ========== STRING SANITIZATION ==========

    /**
     * Remove all whitespace
     */
    public static String removeWhitespace(String str) {
        return str != null ? str.replaceAll("\\s", "") : "";
    }

    /**
     * Remove special characters
     */
    public static String removeSpecialChars(String str) {
        return str != null ? str.replaceAll("[^a-zA-Z0-9\\s]", "") : "";
    }

    /**
     * Remove HTML tags
     */
    public static String removeHtmlTags(String str) {
        return str != null ? str.replaceAll("<[^>]*>", "") : "";
    }

    /**
     * Escape HTML characters
     */
    public static String escapeHtml(String str) {
        if (str == null)
            return "";
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Unescape HTML characters
     */
    public static String unescapeHtml(String str) {
        if (str == null)
            return "";
        return str.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
    }

    /**
     * Sanitize for SQL injection
     */
    public static String sanitizeForSql(String str) {
        if (str == null)
            return "";
        return str.replace("'", "''")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    // ========== STRING SEARCHING ==========

    /**
     * Check if string contains substring (case-insensitive)
     */
    public static boolean containsIgnoreCase(String str, String substring) {
        if (str == null || substring == null)
            return false;
        return str.toLowerCase().contains(substring.toLowerCase());
    }

    /**
     * Check if string starts with prefix (case-insensitive)
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null)
            return false;
        return str.toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * Check if string ends with suffix (case-insensitive)
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null)
            return false;
        return str.toLowerCase().endsWith(suffix.toLowerCase());
    }

    /**
     * Count occurrences of substring
     */
    public static int countOccurrences(String str, String substring) {
        if (isEmpty(str) || isEmpty(substring))
            return 0;

        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }

    /**
     * Find all occurrences of substring
     */
    public static List<Integer> findAllOccurrences(String str, String substring) {
        List<Integer> positions = new ArrayList<>();
        if (isEmpty(str) || isEmpty(substring))
            return positions;

        int index = 0;
        while ((index = str.indexOf(substring, index)) != -1) {
            positions.add(index);
            index += substring.length();
        }
        return positions;
    }

    // ========== STRING SPLITTING ==========

    /**
     * Split string by delimiter
     */
    public static List<String> split(String str, String delimiter) {
        if (isEmpty(str))
            return Collections.emptyList();
        if (delimiter == null)
            return Collections.singletonList(str);

        return Arrays.asList(str.split(Pattern.quote(delimiter)));
    }

    /**
     * Split string by delimiter with limit
     */
    public static List<String> split(String str, String delimiter, int limit) {
        if (isEmpty(str))
            return Collections.emptyList();
        if (delimiter == null)
            return Collections.singletonList(str);

        return Arrays.asList(str.split(Pattern.quote(delimiter), limit));
    }

    /**
     * Split string by whitespace
     */
    public static List<String> splitByWhitespace(String str) {
        if (isEmpty(str))
            return Collections.emptyList();
        return Arrays.asList(str.split("\\s+"));
    }

    /**
     * Split string by comma
     */
    public static List<String> splitByComma(String str) {
        return split(str, ",");
    }

    // ========== STRING JOINING ==========

    /**
     * Join collection of strings
     */
    public static String join(Collection<String> strings, String delimiter) {
        if (strings == null || strings.isEmpty())
            return "";
        if (delimiter == null)
            delimiter = "";

        return strings.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Join array of strings
     */
    public static String join(String[] strings, String delimiter) {
        if (strings == null || strings.length == 0)
            return "";
        return join(Arrays.asList(strings), delimiter);
    }

    /**
     * Join strings with comma
     */
    public static String joinWithComma(String... strings) {
        return join(Arrays.asList(strings), ",");
    }

    /**
     * Join strings with space
     */
    public static String joinWithSpace(String... strings) {
        return join(Arrays.asList(strings), " ");
    }

    // ========== STRING SUBSTITUTION ==========

    /**
     * Replace all occurrences
     */
    public static String replace(String str, String target, String replacement) {
        if (str == null)
            return "";
        if (target == null)
            return str;
        if (replacement == null)
            replacement = "";

        return str.replace(target, replacement);
    }

    /**
     * Replace all occurrences (case-insensitive)
     */
    public static String replaceIgnoreCase(String str, String target, String replacement) {
        if (str == null)
            return "";
        if (target == null)
            return str;
        if (replacement == null)
            replacement = "";

        return str.replaceAll("(?i)" + Pattern.quote(target), replacement);
    }

    /**
     * Replace first occurrence
     */
    public static String replaceFirst(String str, String target, String replacement) {
        if (str == null)
            return "";
        if (target == null)
            return str;
        if (replacement == null)
            replacement = "";

        return str.replaceFirst(Pattern.quote(target), replacement);
    }

    // ========== STRING UTILITIES ==========

    /**
     * Get string length safely
     */
    public static int length(String str) {
        return str != null ? str.length() : 0;
    }

    /**
     * Reverse string
     */
    public static String reverse(String str) {
        if (str == null)
            return "";
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * Repeat string n times
     */
    public static String repeat(String str, int times) {
        if (str == null || times <= 0)
            return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Truncate string to max length
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength)
            return str;
        return str.substring(0, maxLength);
    }

    /**
     * Truncate string with ellipsis
     */
    public static String truncateWithEllipsis(String str, int maxLength) {
        if (str == null || str.length() <= maxLength)
            return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Get substring safely
     */
    public static String substring(String str, int start, int end) {
        if (str == null)
            return "";
        if (start < 0)
            start = 0;
        if (end > str.length())
            end = str.length();
        if (start >= end)
            return "";

        return str.substring(start, end);
    }

    /**
     * Get substring safely
     */
    public static String substring(String str, int start) {
        if (str == null)
            return "";
        if (start < 0)
            start = 0;
        if (start >= str.length())
            return "";

        return str.substring(start);
    }
}
