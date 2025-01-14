package com.bankit.loan.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a loan record in the system.
 * Contains full loan details including customer information, loan terms,
 * payment calculations and the assigned officer.
 */
public class Loan {
    private String loanId;
    private String customerId;
    private String customerName;
    private String contact;
    private String email;
    private String address;
    private AccountType accountType;
    private double loanAmount;
    private double interestRate;
    private int termMonths;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private double monthlyPayment;
    private double totalPayment;
    private Officer officer;

    /**
     * Default constructor.
     * Initializes a new loan with current date as issue date.
     */
    public Loan() {
        this.issueDate = LocalDate.now();
    }

    /**
     * Full constructor with all fields.
     * Validates all inputs for data integrity.
     */
    public Loan(String loanId, String customerId, String customerName, String contact, String email,
                String address, AccountType accountType, double loanAmount, double interestRate,
                int termMonths, LocalDate issueDate, LocalDate dueDate, double monthlyPayment,
                double totalPayment, Officer officer) {
        setLoanId(loanId);
        setCustomerId(customerId);
        setCustomerName(customerName);
        setContact(contact);
        setEmail(email);
        setAddress(address);
        setAccountType(accountType);
        setLoanAmount(loanAmount);
        setInterestRate(interestRate);
        setTermMonths(termMonths);
        setIssueDate(issueDate);
        setDueDate(dueDate);
        setMonthlyPayment(monthlyPayment);
        setTotalPayment(totalPayment);
        setOfficer(officer);
    }

    // Getters and Setters with validation
    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        if (loanId == null || loanId.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan ID cannot be empty");
        }
        if (!loanId.matches("^L\\d{5}$")) {
            throw new IllegalArgumentException("Loan ID must start with 'L' followed by 5 digits");
        }
        this.loanId = loanId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty");
        }
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (customerName.trim().length() < 2) {
            throw new IllegalArgumentException("Customer name must be at least 2 characters");
        }
        this.customerName = customerName.trim();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact cannot be empty");
        }
        if (!contact.matches("^09\\d{9}$")) {
            throw new IllegalArgumentException("Contact must start with 09 and be 11 digits");
        }
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        this.address = address.trim();
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = Objects.requireNonNull(accountType, "Account type cannot be null");
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        if (loanAmount <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than 0");
        }
        if (loanAmount > 1000000000) { // 1 billion limit
            throw new IllegalArgumentException("Loan amount exceeds maximum limit");
        }
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        if (interestRate > 100) {
            throw new IllegalArgumentException("Interest rate cannot exceed 100%");
        }
        this.interestRate = interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        if (termMonths <= 0) {
            throw new IllegalArgumentException("Term months must be greater than 0");
        }
        if (termMonths > 360) { // 30 years
            throw new IllegalArgumentException("Term months cannot exceed 360");
        }
        this.termMonths = termMonths;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = Objects.requireNonNull(issueDate, "Issue date cannot be null");
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null");
        if (dueDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Due date cannot be before issue date");
        }
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        if (monthlyPayment <= 0) {
            throw new IllegalArgumentException("Monthly payment must be greater than 0");
        }
        this.monthlyPayment = monthlyPayment;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        if (totalPayment <= 0) {
            throw new IllegalArgumentException("Total payment must be greater than 0");
        }
        if (totalPayment < loanAmount) {
            throw new IllegalArgumentException("Total payment cannot be less than loan amount");
        }
        this.totalPayment = totalPayment;
    }

    public Officer getOfficer() {
        return officer;
    }

    public void setOfficer(Officer officer) {
        this.officer = Objects.requireNonNull(officer, "Officer cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(loanId, loan.loanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanId);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId='" + loanId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", loanAmount=" + loanAmount +
                ", interestRate=" + interestRate +
                ", termMonths=" + termMonths +
                '}';
    }
}