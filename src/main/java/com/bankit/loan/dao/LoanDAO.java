package com.bankit.loan.dao;

import com.bankit.loan.model.Loan;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Loan operations
 */
public interface LoanDAO {
    /**
     * Saves a new loan record
     *
     * @param loan the loan to save
     * @throws SQLException if a database error occurs
     */
    void save(Loan loan) throws SQLException;

    /**
     * Updates an existing loan record
     *
     * @param loan the loan to update
     * @throws SQLException if a database error occurs
     */
    void update(Loan loan) throws SQLException;

    /**
     * Deletes a loan record by ID
     *
     * @param loanId the ID of the loan to delete
     * @throws SQLException if a database error occurs
     */
    void delete(String loanId) throws SQLException;

    /**
     * Finds a loan by its ID
     *
     * @param loanId the ID to search for
     * @return Optional containing the loan if found
     * @throws SQLException if a database error occurs
     */
    Optional<Loan> findById(String loanId) throws SQLException;

    /**
     * Retrieves all loan records
     *
     * @return List of all loans
     * @throws SQLException if a database error occurs
     */
    List<Loan> findAll() throws SQLException;

    /**
     * Finds loans by customer name
     *
     * @param customerName the name to search for
     * @return List of matching loans
     * @throws SQLException if a database error occurs
     */
    List<Loan> findByCustomerName(String customerName) throws SQLException;
}