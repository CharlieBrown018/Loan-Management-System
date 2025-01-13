package com.bankit.loan.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database configuration and connection management
 * Handles database connection lifecycle and initialization
 */
public class DatabaseConfig {
    private static Connection connection;

    /**
     * Gets a connection to the database
     * Creates a new connection if one doesn't exist or is closed
     *
     * @return A Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(AppConfig.getDatabaseUrl());
            initializeDatabase();
        }
        return connection;
    }

    /**
     * Initializes the database schema if it doesn't exist
     *
     * @throws SQLException if a database access error occurs
     */
    private static void initializeDatabase() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Enable foreign keys
            statement.execute("PRAGMA foreign_keys = ON");

            // Create Officers table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS officers (
                    officer_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    contact TEXT NOT NULL
                )
            """);

            // Create Loans table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS loans (
                    loan_id TEXT PRIMARY KEY,
                    customer_id TEXT NOT NULL,
                    customer_name TEXT NOT NULL,
                    contact TEXT NOT NULL,
                    email TEXT NOT NULL,
                    address TEXT NOT NULL,
                    account_type TEXT NOT NULL,
                    loan_amount REAL NOT NULL,
                    interest_rate REAL NOT NULL,
                    term_months INTEGER NOT NULL,
                    issue_date TEXT NOT NULL,
                    due_date TEXT NOT NULL,
                    monthly_payment REAL NOT NULL,
                    total_payment REAL NOT NULL,
                    officer_id TEXT NOT NULL,
                    FOREIGN KEY (officer_id) REFERENCES officers(officer_id)
                )
            """);
        }
    }

    /**
     * Closes the database connection if it's open
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Begins a transaction
     *
     * @throws SQLException if a database access error occurs
     */
    public static void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }

    /**
     * Commits the current transaction
     *
     * @throws SQLException if a database access error occurs
     */
    public static void commitTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    /**
     * Rolls back the current transaction
     *
     * @throws SQLException if a database access error occurs
     */
    public static void rollbackTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
            connection.setAutoCommit(true);
        }
    }
}