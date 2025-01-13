package com.bankit.loan.model;

/**
 * Represents account types available in the system
 */
public enum AccountType {
    SAVINGS("Savings"),
    CHECKING("Checking"),
    JOINT("Joint");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

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
        for (AccountType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        return null;
    }
}