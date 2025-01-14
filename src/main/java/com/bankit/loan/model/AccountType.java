package com.bankit.loan.model;

/**
 * Represents account types available in the system.
 * Provides display names and lookup functionality.
 */
public enum AccountType {
    SAVINGS("Savings"),
    CHECKING("Checking"),
    JOINT("Joint");

    private final String displayName;

    /**
     * Constructor for account type with display name
     */
    AccountType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for the account type
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Gets AccountType from display name
     *
     * @param displayName the display name to look up
     * @return the matching AccountType or null if not found
     */
    public static AccountType fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }

        String normalized = displayName.trim().toLowerCase();
        for (AccountType type : values()) {
            if (type.getDisplayName().toLowerCase().equals(normalized)) {
                return type;
            }
        }
        return null;
    }
}