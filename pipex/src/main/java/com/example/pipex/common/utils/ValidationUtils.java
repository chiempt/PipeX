package com.example.pipex.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Validation utilities với enterprise-grade features
 * - Email, phone, URL validation
 * - Custom validation rules
 * - Null-safe validation chains
 */
@Slf4j
public final class ValidationUtils {

    private ValidationUtils() {
        // Utility class
    }

    // ========== COMMON PATTERNS ==========

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s\\-\\(\\)]{7,20}$");

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile(
            "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|3[0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12})$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    // ========== EMAIL VALIDATION ==========

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate email address with custom pattern
     */
    public static boolean isValidEmail(String email, Pattern pattern) {
        return email != null && pattern.matcher(email).matches();
    }

    /**
     * Validate multiple email addresses
     */
    public static boolean areValidEmails(String... emails) {
        if (emails == null)
            return false;
        for (String email : emails) {
            if (!isValidEmail(email)) {
                return false;
            }
        }
        return true;
    }

    // ========== PHONE VALIDATION ==========

    /**
     * Validate phone number
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate phone number with custom pattern
     */
    public static boolean isValidPhone(String phone, Pattern pattern) {
        return phone != null && pattern.matcher(phone).matches();
    }

    /**
     * Validate phone number for specific country
     */
    public static boolean isValidPhoneForCountry(String phone, String countryCode) {
        if (phone == null || countryCode == null)
            return false;

        // Simple country-specific validation
        switch (countryCode.toUpperCase()) {
            case "US":
                return phone.matches("^\\+?1?[2-9]\\d{2}[2-9]\\d{2}\\d{4}$");
            case "UK":
                return phone.matches("^\\+?44\\s?\\d{4}\\s?\\d{6}$");
            case "VN":
                return phone.matches("^\\+?84\\s?[1-9]\\d{8}$");
            default:
                return isValidPhone(phone);
        }
    }

    // ========== URL VALIDATION ==========

    /**
     * Validate URL
     */
    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    /**
     * Validate URL with custom pattern
     */
    public static boolean isValidUrl(String url, Pattern pattern) {
        return url != null && pattern.matcher(url).matches();
    }

    /**
     * Validate HTTP/HTTPS URL only
     */
    public static boolean isValidHttpUrl(String url) {
        if (url == null)
            return false;
        return url.startsWith("http://") || url.startsWith("https://");
    }

    // ========== IP ADDRESS VALIDATION ==========

    /**
     * Validate IPv4 address
     */
    public static boolean isValidIPv4(String ip) {
        return ip != null && IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * Validate IPv6 address
     */
    public static boolean isValidIPv6(String ip) {
        return ip != null && IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * Validate IP address (IPv4 or IPv6)
     */
    public static boolean isValidIP(String ip) {
        return isValidIPv4(ip) || isValidIPv6(ip);
    }

    // ========== UUID VALIDATION ==========

    /**
     * Validate UUID
     */
    public static boolean isValidUUID(String uuid) {
        return uuid != null && UUID_PATTERN.matcher(uuid).matches();
    }

    /**
     * Validate UUID version
     */
    public static boolean isValidUUIDVersion(String uuid, int version) {
        if (!isValidUUID(uuid))
            return false;

        char versionChar = uuid.charAt(14);
        return Character.digit(versionChar, 16) == version;
    }

    // ========== CREDIT CARD VALIDATION ==========

    /**
     * Validate credit card number
     */
    public static boolean isValidCreditCard(String cardNumber) {
        if (cardNumber == null)
            return false;

        // Remove spaces and dashes
        String cleanNumber = cardNumber.replaceAll("[\\s-]", "");

        // Check pattern
        if (!CREDIT_CARD_PATTERN.matcher(cleanNumber).matches()) {
            return false;
        }

        // Luhn algorithm validation
        return isValidLuhn(cleanNumber);
    }

    /**
     * Validate credit card using Luhn algorithm
     */
    private static boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }

    // ========== PASSWORD VALIDATION ==========

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validate password with custom requirements
     */
    public static boolean isValidPassword(String password, int minLength, boolean requireUppercase,
            boolean requireLowercase, boolean requireDigit, boolean requireSpecial) {
        if (password == null || password.length() < minLength)
            return false;

        boolean hasUppercase = !requireUppercase || password.matches(".*[A-Z].*");
        boolean hasLowercase = !requireLowercase || password.matches(".*[a-z].*");
        boolean hasDigit = !requireDigit || password.matches(".*\\d.*");
        boolean hasSpecial = !requireSpecial || password.matches(".*[@$!%*?&].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    // ========== STRING VALIDATION ==========

    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Check if string is not null and not blank
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Check if string has minimum length
     */
    public static boolean hasMinLength(String str, int minLength) {
        return str != null && str.length() >= minLength;
    }

    /**
     * Check if string has maximum length
     */
    public static boolean hasMaxLength(String str, int maxLength) {
        return str != null && str.length() <= maxLength;
    }

    /**
     * Check if string has length in range
     */
    public static boolean hasLengthInRange(String str, int minLength, int maxLength) {
        return str != null && str.length() >= minLength && str.length() <= maxLength;
    }

    /**
     * Check if string contains only letters
     */
    public static boolean isAlpha(String str) {
        return str != null && str.matches("[a-zA-Z]+");
    }

    /**
     * Check if string contains only digits
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    /**
     * Check if string contains only alphanumeric characters
     */
    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("[a-zA-Z0-9]+");
    }

    // ========== NUMBER VALIDATION ==========

    /**
     * Check if string is valid integer
     */
    public static boolean isValidInteger(String str) {
        if (str == null)
            return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if string is valid long
     */
    public static boolean isValidLong(String str) {
        if (str == null)
            return false;
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if string is valid double
     */
    public static boolean isValidDouble(String str) {
        if (str == null)
            return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if string is valid float
     */
    public static boolean isValidFloat(String str) {
        if (str == null)
            return false;
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ========== RANGE VALIDATION ==========

    /**
     * Check if number is in range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Check if number is in range
     */
    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    /**
     * Check if number is in range
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Check if number is in range
     */
    public static boolean isInRange(float value, float min, float max) {
        return value >= min && value <= max;
    }

    // ========== DATE VALIDATION ==========

    /**
     * Check if year is valid
     */
    public static boolean isValidYear(int year) {
        return year >= 1900 && year <= 2100;
    }

    /**
     * Check if month is valid
     */
    public static boolean isValidMonth(int month) {
        return month >= 1 && month <= 12;
    }

    /**
     * Check if day is valid for given month and year
     */
    public static boolean isValidDay(int day, int month, int year) {
        if (!isValidYear(year) || !isValidMonth(month))
            return false;

        int[] daysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        // Check for leap year
        if (month == 2 && isLeapYear(year)) {
            daysInMonth[1] = 29;
        }

        return day >= 1 && day <= daysInMonth[month - 1];
    }

    /**
     * Check if year is leap year
     */
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // ========== CUSTOM VALIDATION ==========

    /**
     * Validate using custom pattern
     */
    public static boolean matchesPattern(String str, Pattern pattern) {
        return str != null && pattern.matcher(str).matches();
    }

    /**
     * Validate using custom regex
     */
    public static boolean matchesRegex(String str, String regex) {
        return str != null && str.matches(regex);
    }

    /**
     * Validate using custom regex with flags
     */
    public static boolean matchesRegex(String str, String regex, int flags) {
        if (str == null)
            return false;
        Pattern pattern = Pattern.compile(regex, flags);
        return pattern.matcher(str).matches();
    }

    // ========== VALIDATION CHAINS ==========

    /**
     * Validation result builder
     */
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();

        public ValidationResult addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
            return this;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrors() {
            return errors.toString();
        }
    }

    /**
     * Validate multiple conditions
     */
    public static ValidationResult validate(Object... validations) {
        ValidationResult result = new ValidationResult();

        for (int i = 0; i < validations.length; i += 2) {
            if (i + 1 < validations.length) {
                boolean condition = (Boolean) validations[i];
                String error = (String) validations[i + 1];

                if (!condition) {
                    result.addError(error);
                }
            }
        }

        return result;
    }

    // ========== BUSINESS VALIDATION ==========

    /**
     * Validate business hours (24-hour format)
     */
    public static boolean isValidBusinessHours(int hour) {
        return hour >= 0 && hour <= 23;
    }

    /**
     * Validate business minutes
     */
    public static boolean isValidBusinessMinutes(int minute) {
        return minute >= 0 && minute <= 59;
    }

    /**
     * Validate business seconds
     */
    public static boolean isValidBusinessSeconds(int second) {
        return second >= 0 && second <= 59;
    }

    /**
     * Validate percentage
     */
    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0.0 && percentage <= 100.0;
    }

    /**
     * Validate positive number
     */
    public static boolean isPositive(Number number) {
        return number != null && number.doubleValue() > 0;
    }

    /**
     * Validate non-negative number
     */
    public static boolean isNonNegative(Number number) {
        return number != null && number.doubleValue() >= 0;
    }
}
