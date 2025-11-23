package com.example.pipex.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Date and time manipulation utilities với enterprise-grade features
 * - LocalDateTime/ZonedDateTime operations
 * - Formatting và parsing với timezone support
 * - Date range calculations
 * - Comparison utilities
 */
@Slf4j
public final class DateUtils {

    private DateUtils() {
        // Utility class
    }

    // ========== COMMON FORMATTERS ==========

    public static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ISO_LOCAL_TIME;
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DD_MM_YYYY_HH_MM_SS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    // ========== CURRENT DATE/TIME ==========

    /**
     * Get current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Get current LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Get current LocalTime
     */
    public static LocalTime currentTime() {
        return LocalTime.now();
    }

    /**
     * Get current ZonedDateTime in system default timezone
     */
    public static ZonedDateTime nowWithZone() {
        return ZonedDateTime.now();
    }

    /**
     * Get current ZonedDateTime in specified timezone
     */
    public static ZonedDateTime nowWithZone(String zoneId) {
        return ZonedDateTime.now(ZoneId.of(zoneId));
    }

    /**
     * Get current timestamp as milliseconds
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    // ========== DATE CREATION ==========

    /**
     * Create LocalDateTime from components
     */
    public static LocalDateTime of(int year, int month, int day, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    /**
     * Create LocalDate from components
     */
    public static LocalDate ofDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    /**
     * Create LocalTime from components
     */
    public static LocalTime ofTime(int hour, int minute, int second) {
        return LocalTime.of(hour, minute, second);
    }

    /**
     * Create LocalDateTime from Date
     */
    public static LocalDateTime fromDate(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    /**
     * Create Date from LocalDateTime
     */
    public static Date toDate(LocalDateTime dateTime) {
        return dateTime != null ? Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    // ========== DATE PARSING ==========

    /**
     * Parse LocalDateTime from string
     */
    public static Optional<LocalDateTime> parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, ISO_DATE_TIME);
    }

    /**
     * Parse LocalDateTime from string with formatter
     */
    public static Optional<LocalDateTime> parseDateTime(String dateTimeStr, DateTimeFormatter formatter) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(LocalDateTime.parse(dateTimeStr.trim(), formatter));
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date time '{}' with formatter '{}': {}",
                    dateTimeStr, formatter, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Parse LocalDate from string
     */
    public static Optional<LocalDate> parseDate(String dateStr) {
        return parseDate(dateStr, ISO_DATE);
    }

    /**
     * Parse LocalDate from string with formatter
     */
    public static Optional<LocalDate> parseDate(String dateStr, DateTimeFormatter formatter) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(LocalDate.parse(dateStr.trim(), formatter));
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date '{}' with formatter '{}': {}",
                    dateStr, formatter, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Parse LocalTime from string
     */
    public static Optional<LocalTime> parseTime(String timeStr) {
        return parseTime(timeStr, ISO_TIME);
    }

    /**
     * Parse LocalTime from string with formatter
     */
    public static Optional<LocalTime> parseTime(String timeStr, DateTimeFormatter formatter) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(LocalTime.parse(timeStr.trim(), formatter));
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse time '{}' with formatter '{}': {}",
                    timeStr, formatter, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Parse ZonedDateTime from string
     */
    public static Optional<ZonedDateTime> parseZonedDateTime(String zonedDateTimeStr) {
        if (zonedDateTimeStr == null || zonedDateTimeStr.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(ZonedDateTime.parse(zonedDateTimeStr.trim()));
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse zoned date time '{}': {}", zonedDateTimeStr, e.getMessage());
            return Optional.empty();
        }
    }

    // ========== DATE FORMATTING ==========

    /**
     * Format LocalDateTime to string
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, ISO_DATE_TIME);
    }

    /**
     * Format LocalDateTime to string with formatter
     */
    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime != null ? dateTime.format(formatter) : "";
    }

    /**
     * Format LocalDate to string
     */
    public static String format(LocalDate date) {
        return format(date, ISO_DATE);
    }

    /**
     * Format LocalDate to string with formatter
     */
    public static String format(LocalDate date, DateTimeFormatter formatter) {
        return date != null ? date.format(formatter) : "";
    }

    /**
     * Format LocalTime to string
     */
    public static String format(LocalTime time) {
        return format(time, ISO_TIME);
    }

    /**
     * Format LocalTime to string with formatter
     */
    public static String format(LocalTime time, DateTimeFormatter formatter) {
        return time != null ? time.format(formatter) : "";
    }

    /**
     * Format ZonedDateTime to string
     */
    public static String format(ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) : "";
    }

    /**
     * Format ZonedDateTime to string with formatter
     */
    public static String format(ZonedDateTime zonedDateTime, DateTimeFormatter formatter) {
        return zonedDateTime != null ? zonedDateTime.format(formatter) : "";
    }

    // ========== DATE CALCULATIONS ==========

    /**
     * Add days to LocalDateTime
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        return dateTime != null ? dateTime.plusDays(days) : null;
    }

    /**
     * Add days to LocalDate
     */
    public static LocalDate addDays(LocalDate date, long days) {
        return date != null ? date.plusDays(days) : null;
    }

    /**
     * Add hours to LocalDateTime
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return dateTime != null ? dateTime.plusHours(hours) : null;
    }

    /**
     * Add minutes to LocalDateTime
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime != null ? dateTime.plusMinutes(minutes) : null;
    }

    /**
     * Add seconds to LocalDateTime
     */
    public static LocalDateTime addSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime != null ? dateTime.plusSeconds(seconds) : null;
    }

    /**
     * Add months to LocalDateTime
     */
    public static LocalDateTime addMonths(LocalDateTime dateTime, long months) {
        return dateTime != null ? dateTime.plusMonths(months) : null;
    }

    /**
     * Add years to LocalDateTime
     */
    public static LocalDateTime addYears(LocalDateTime dateTime, long years) {
        return dateTime != null ? dateTime.plusYears(years) : null;
    }

    /**
     * Subtract days from LocalDateTime
     */
    public static LocalDateTime subtractDays(LocalDateTime dateTime, long days) {
        return dateTime != null ? dateTime.minusDays(days) : null;
    }

    /**
     * Subtract days from LocalDate
     */
    public static LocalDate subtractDays(LocalDate date, long days) {
        return date != null ? date.minusDays(days) : null;
    }

    /**
     * Subtract hours from LocalDateTime
     */
    public static LocalDateTime subtractHours(LocalDateTime dateTime, long hours) {
        return dateTime != null ? dateTime.minusHours(hours) : null;
    }

    /**
     * Subtract minutes from LocalDateTime
     */
    public static LocalDateTime subtractMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime != null ? dateTime.minusMinutes(minutes) : null;
    }

    /**
     * Subtract seconds from LocalDateTime
     */
    public static LocalDateTime subtractSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime != null ? dateTime.minusSeconds(seconds) : null;
    }

    /**
     * Subtract months from LocalDateTime
     */
    public static LocalDateTime subtractMonths(LocalDateTime dateTime, long months) {
        return dateTime != null ? dateTime.minusMonths(months) : null;
    }

    /**
     * Subtract years from LocalDateTime
     */
    public static LocalDateTime subtractYears(LocalDateTime dateTime, long years) {
        return dateTime != null ? dateTime.minusYears(years) : null;
    }

    // ========== DATE DIFFERENCES ==========

    /**
     * Calculate days between two LocalDateTime
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return 0;
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calculate days between two LocalDate
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            return 0;
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calculate hours between two LocalDateTime
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return 0;
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calculate minutes between two LocalDateTime
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return 0;
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Calculate seconds between two LocalDateTime
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return 0;
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * Calculate months between two LocalDateTime
     */
    public static long monthsBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return 0;
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * Calculate years between two LocalDateTime
     */
    public static long yearsBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null)
            return 0;
        return ChronoUnit.YEARS.between(start, end);
    }

    // ========== DATE COMPARISONS ==========

    /**
     * Check if first date is before second date
     */
    public static boolean isBefore(LocalDateTime first, LocalDateTime second) {
        return first != null && second != null && first.isBefore(second);
    }

    /**
     * Check if first date is after second date
     */
    public static boolean isAfter(LocalDateTime first, LocalDateTime second) {
        return first != null && second != null && first.isAfter(second);
    }

    /**
     * Check if first date is before or equal to second date
     */
    public static boolean isBeforeOrEqual(LocalDateTime first, LocalDateTime second) {
        return first != null && second != null && !first.isAfter(second);
    }

    /**
     * Check if first date is after or equal to second date
     */
    public static boolean isAfterOrEqual(LocalDateTime first, LocalDateTime second) {
        return first != null && second != null && !first.isBefore(second);
    }

    /**
     * Check if date is in range
     */
    public static boolean isInRange(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return date != null && start != null && end != null &&
                !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }

    /**
     * Check if date is yesterday
     */
    public static boolean isYesterday(LocalDate date) {
        return date != null && date.equals(LocalDate.now().minusDays(1));
    }

    /**
     * Check if date is tomorrow
     */
    public static boolean isTomorrow(LocalDate date) {
        return date != null && date.equals(LocalDate.now().plusDays(1));
    }

    // ========== DATE UTILITIES ==========

    /**
     * Get start of day
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * Get end of day
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date != null ? date.atTime(LocalTime.MAX) : null;
    }

    /**
     * Get start of month
     */
    public static LocalDateTime startOfMonth(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0) : null;
    }

    /**
     * Get end of month
     */
    public static LocalDateTime endOfMonth(LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        return dateTime.withDayOfMonth(dateTime.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
    }

    /**
     * Get start of year
     */
    public static LocalDateTime startOfYear(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0) : null;
    }

    /**
     * Get end of year
     */
    public static LocalDateTime endOfYear(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.withDayOfYear(dateTime.toLocalDate().lengthOfYear())
                .withHour(23).withMinute(59).withSecond(59).withNano(999_999_999) : null;
    }

    /**
     * Get age in years
     */
    public static int getAge(LocalDate birthDate) {
        if (birthDate == null)
            return 0;
        return (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

    /**
     * Get age in years at specific date
     */
    public static int getAge(LocalDate birthDate, LocalDate atDate) {
        if (birthDate == null || atDate == null)
            return 0;
        return (int) ChronoUnit.YEARS.between(birthDate, atDate);
    }

    // ========== TIMEZONE UTILITIES ==========

    /**
     * Convert LocalDateTime to ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime dateTime, String zoneId) {
        return dateTime != null ? dateTime.atZone(ZoneId.of(zoneId)) : null;
    }

    /**
     * Convert ZonedDateTime to LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? zonedDateTime.toLocalDateTime() : null;
    }

    /**
     * Convert LocalDateTime to different timezone
     */
    public static LocalDateTime convertTimezone(LocalDateTime dateTime, String fromZone, String toZone) {
        if (dateTime == null)
            return null;
        return dateTime.atZone(ZoneId.of(fromZone))
                .withZoneSameInstant(ZoneId.of(toZone))
                .toLocalDateTime();
    }

    // ========== VALIDATION ==========

    /**
     * Check if year is leap year
     */
    public static boolean isLeapYear(int year) {
        return Year.of(year).isLeap();
    }

    /**
     * Check if date is valid
     */
    public static boolean isValidDate(int year, int month, int day) {
        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if time is valid
     */
    public static boolean isValidTime(int hour, int minute, int second) {
        try {
            LocalTime.of(hour, minute, second);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
