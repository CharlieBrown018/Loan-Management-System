package com.bankit.loan.service;

import com.bankit.loan.model.Officer;

import java.sql.SQLException;
import java.util.List;

/**
 * Service interface for officer-related business operations
 */
public interface OfficerService {
    /**
     * Creates a new officer
     *
     * @param officer the officer to create
     * @throws SQLException if database operation fails
     * @throws IllegalArgumentException if officer data is invalid
     */
    void createOfficer(Officer officer) throws SQLException;

    /**
     * Updates an existing officer
     *
     * @param officer the officer to update
     * @throws SQLException if database operation fails
     * @throws IllegalArgumentException if officer data is invalid
     */
    void updateOfficer(Officer officer) throws SQLException;

    /**
     * Deletes an officer by ID
     *
     * @param officerId the ID of the officer to delete
     * @throws SQLException if database operation fails
     */
    void deleteOfficer(String officerId) throws SQLException;

    /**
     * Retrieves an officer by ID
     *
     * @param officerId the ID of the officer to retrieve
     * @return the officer if found, null otherwise
     * @throws SQLException if database operation fails
     */
    Officer getOfficerById(String officerId) throws SQLException;

    /**
     * Retrieves all officers
     *
     * @return list of all officers
     * @throws SQLException if database operation fails
     */
    List<Officer> getAllOfficers() throws SQLException;

    /**
     * Validates officer data
     *
     * @param officer the officer data to validate
     * @throws IllegalArgumentException if data is invalid
     */
    void validateOfficer(Officer officer);

    /**
     * Generates a unique officer ID
     *
     * @return a unique officer ID
     */
    String generateOfficerId();
}