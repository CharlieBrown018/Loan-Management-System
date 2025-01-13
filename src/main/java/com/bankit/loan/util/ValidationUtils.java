package com.bankit.loan.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^09\\d{9}$"
    );

    private static final Pattern CUSTOMER_ID_PATTERN = Pattern.compile(
            "^C\\d{5}$"
    );

    private static final Pattern LOAN_ID_PATTERN = Pattern.compile(
            "^L\\d{5}$"
    );

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM--dd--yyyy");

    /**
     * Validates that a string is not null or empty
     *
     * @param value the string to validate
     * @param fieldName the name of the field being validated
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    /**
     * Validates email format
     *
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates phone number format
     *
     * @param phone the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validates customer ID format
     *
     * @param customerId the customer ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCustomerId(String customerId) {
        return customerId != null && CUSTOMER_ID_PATTERN.matcher(customerId).matches();
    }

    /**
     * Validates loan ID format
     *
     * @param loanId the loan ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidLoanId(String loanId) {
        return loanId != null && LOAN_ID_PATTERN.matcher(loanId).matches();
    }

    /**
     * Validates date string format
     *
     * @param dateStr the date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates number is positive
     *
     * @param value the number to validate
     * @param fieldName the name of the field being validated
     * @throws IllegalArgumentException if validation fails
     */
    public static void validatePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0");
        }
    }

    /**
     * Validates number is non-negative
     *
     * @param value the number to validate
     * @param fieldName the name of the field being validated
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    /**
     * Validates object is not null
     *
     * @param value the object to validate
     * @param fieldName the name of the field being validated
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    /**
     * Parses date string to LocalDate
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate
     * @throws IllegalArgumentException if parsing fails
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use MM--dd--yyyy");
        }
    }

    /**
     * Formats LocalDate to string
     *
     * @param date the date to format
     * @return the formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}