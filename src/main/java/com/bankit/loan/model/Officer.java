package com.bankit.loan.model;

import java.util.Objects;

/**
 * Represents a loan officer in the system.
 * Manages officer details and validation.
 */
public class Officer {
    private String officerId;
    private String name;
    private String email;
    private String contact;

    /**
     * Default constructor initializing empty fields
     */
    public Officer() {
        this.officerId = "";
        this.name = "";
        this.email = "";
        this.contact = "";
    }

    /**
     * Constructor with all fields and validation
     */
    public Officer(String officerId, String name, String email, String contact) {
        setOfficerId(officerId);
        setName(name);
        setEmail(email);
        setContact(contact);
    }

    public String getOfficerId() {
        return officerId;
    }

    public void setOfficerId(String officerId) {
        if (officerId == null || officerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Officer ID cannot be empty");
        }
        if (!officerId.matches("^OFF\\d{4}$")) {
            throw new IllegalArgumentException("Officer ID must start with 'OFF' followed by 4 digits");
        }
        this.officerId = officerId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Officer name cannot be empty");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Officer name must be at least 2 characters");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Officer email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Officer contact cannot be empty");
        }
        if (!contact.matches("^09\\d{9}$")) {
            throw new IllegalArgumentException("Contact must start with 09 and be 11 digits");
        }
        this.contact = contact;
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
        return String.format("%s - %s", officerId, name);
    }
}