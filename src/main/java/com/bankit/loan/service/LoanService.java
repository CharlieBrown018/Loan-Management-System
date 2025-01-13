package com.bankit.loan.service;

import com.bankit.loan.model.Loan;

import java.sql.SQLException;
import java.util.List;

/**
 * Service interface for loan-related business operations
 */
public interface LoanService {
    /**
     * Creates a new loan
     *
     * @param loan the loan to create
     * @throws SQLException if database operation fails
     * @throws IllegalArgumentException if loan data is invalid
     */
    void createLoan(Loan loan) throws SQLException;

    /**
     * Updates an existing loan
     *
     * @param loan the loan to update
     * @throws SQLException if database operation fails
     * @throws IllegalArgumentException if loan data is invalid
     */
    void updateLoan(Loan loan) throws SQLException;

    /**
     * Deletes a loan by ID
     *
     * @param loanId the ID of the loan to delete
     * @throws SQLException if database operation fails
     */
    void deleteLoan(String loanId) throws SQLException;

    /**
     * Retrieves a loan by ID
     *
     * @param loanId the ID of the loan to retrieve
     * @return the loan if found, null otherwise
     * @throws SQLException if database operation fails
     */
    Loan getLoanById(String loanId) throws SQLException;

    /**
     * Retrieves all loans
     *
     * @return list of all loans
     * @throws SQLException if database operation fails
     */
    List<Loan> getAllLoans() throws SQLException;

    /**
     * Searches for loans by customer name
     *
     * @param customerName the name to search for
     * @return list of matching loans
     * @throws SQLException if database operation fails
     */
    List<Loan> searchLoansByCustomerName(String customerName) throws SQLException;

    /**
     * Calculates loan payments
     *
     * @param principal the loan amount
     * @param annualInterestRate the annual interest rate
     * @param termInMonths the loan term in months
     * @return array containing [monthlyPayment, totalPayment]
     */
    double[] calculateLoanPayments(double principal, double annualInterestRate, int termInMonths);

    /**
     * Generates a unique loan ID
     *
     * @return a unique loan ID
     */
    String generateLoanId();
}