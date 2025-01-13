package com.bankit.loan.model;

import java.util.Objects;

/**
 * Represents a loan officer in the system
 */
public class Officer {
    private String officerId;
    private String name;
    private String email;
    private String contact;

    // Default constructor
    public Officer() {}

    // Constructor with all fields
    public Officer(String officerId, String name, String email, String contact) {
        this.officerId = Objects.requireNonNull(officerId, "Officer ID cannot be null");
        this.name = Objects.requireNonNull(name, "Officer name cannot be null");
        this.email = Objects.requireNonNull(email, "Officer email cannot be null");
        this.contact = Objects.requireNonNull(contact, "Officer contact cannot be null");
    }

    // Getters and Setters with validation
    public String getOfficerId() {
        return officerId;
    }

    public void setOfficerId(String officerId) {
        this.officerId = Objects.requireNonNull(officerId, "Officer ID cannot be null");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Officer name cannot be null");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Officer email cannot be null");
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = Objects.requireNonNull(contact, "Officer contact cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Officer officer = (Officer) o;
        return Objects.equals(officerId, officer.officerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(officerId);
    }

    @Override
    public String toString() {
        return "Officer{" +
                "officerId='" + officerId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}