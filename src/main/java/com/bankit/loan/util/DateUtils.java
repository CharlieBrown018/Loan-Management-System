package com.bankit.loan.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date operations
 */
public class DateUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("MM--dd--yyyy");

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    /**
     * Gets the current date as a string in default format
     *
     * @return formatted current date
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DEFAULT_FORMATTER);
    }

    /**
     * Gets the current date in display format
     *
     * @return formatted current date for display
     */
    public static String getCurrentDateForDisplay() {
        return LocalDate.now().format(DISPLAY_FORMATTER);
    }

    /**
     * Parses a date string using the default format
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate
     * @throws IllegalArgumentException if parsing fails
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DEFAULT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use MM--dd--yyyy");
        }
    }

    /**
     * Formats a LocalDate using the default format
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DEFAULT_FORMATTER);
    }

    /**
     * Formats a LocalDate for display
     *
     * @param date the date to format
     * @return formatted date string for display
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }

    /**
     * Calculates due date based on issue date and term
     *
     * @param issueDate the loan issue date
     * @param termMonths the loan term in months
     * @return the calculated due date
     */
    public static LocalDate calculateDueDate(LocalDate issueDate, int termMonths) {
        return issueDate.plusMonths(termMonths);
    }

    /**
     * Checks if a date is within valid range
     *
     * @param date the date to check
     * @param minYears minimum years from now (negative for past)
     * @param maxYears maximum years from now (positive for future)
     * @return true if date is within range
     */
    public static boolean isDateInRange(LocalDate date, int minYears, int maxYears) {
        LocalDate minDate = LocalDate.now().plusYears(minYears);
        LocalDate maxDate = LocalDate.now().plusYears(maxYears);
        return !date.isBefore(minDate) && !date.isAfter(maxDate);
    }

    /**
     * Calculates months between two dates
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return number of months between dates
     */
    public static int getMonthsBetween(LocalDate startDate, LocalDate endDate) {
        Period period = Period.between(startDate, endDate);
        return period.getYears() * 12 + period.getMonths();
    }

    /**
     * Checks if a date is a business day (Monday-Friday)
     *
     * @param date the date to check
     * @return true if date is a business day
     */
    public static boolean isBusinessDay(LocalDate date) {
        return !date.getDayOfWeek().toString().equals("SATURDAY") &&
                !date.getDayOfWeek().toString().equals("SUNDAY");
    }

    /**
     * Gets the next business day from a given date
     *
     * @param date the starting date
     * @return the next business day
     */
    public static LocalDate getNextBusinessDay(LocalDate date) {
        LocalDate nextDay = date.plusDays(1);
        while (!isBusinessDay(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }
}