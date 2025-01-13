package com.bankit.loan.dao;

import com.bankit.loan.model.Officer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Officer operations
 */
public interface OfficerDAO {
    /**
     * Saves a new officer record
     *
     * @param officer the officer to save
     * @throws SQLException if a database error occurs
     */
    void save(Officer officer) throws SQLException;

    /**
     * Updates an existing officer record
     *
     * @param officer the officer to update
     * @throws SQLException if a database error occurs
     */
    void update(Officer officer) throws SQLException;

    /**
     * Deletes an officer record by ID
     *
     * @param officerId the ID of the officer to delete
     * @throws SQLException if a database error occurs
     */
    void delete(String officerId) throws SQLException;

    /**
     * Finds an officer by ID
     *
     * @param officerId the ID to search for
     * @return Optional containing the officer if found
     * @throws SQLException if a database error occurs
     */
    Optional<Officer> findById(String officerId) throws SQLException;

    /**
     * Retrieves all officer records
     *
     * @return List of all officers
     * @throws SQLException if a database error occurs
     */
    List<Officer> findAll() throws SQLException;
}