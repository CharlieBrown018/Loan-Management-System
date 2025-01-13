package com.bankit.loan.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a loan record in the system
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

    // Default constructor
    public Loan() {
        this.issueDate = LocalDate.now();
    }

    // Constructor with all fields
    public Loan(String loanId, String customerId, String customerName, String contact, String email,
                String address, AccountType accountType, double loanAmount, double interestRate,
                int termMonths, LocalDate issueDate, LocalDate dueDate, double monthlyPayment,
                double totalPayment, Officer officer) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.accountType = accountType;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.monthlyPayment = monthlyPayment;
        this.totalPayment = totalPayment;
        this.officer = officer;
    }

    // Getters and Setters with validation
    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = Objects.requireNonNull(loanId, "Loan ID cannot be null");
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = Objects.requireNonNull(customerName, "Customer name cannot be null");
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = Objects.requireNonNull(contact, "Contact cannot be null");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = Objects.requireNonNull(address, "Address cannot be null");
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
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
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