// DatabaseConfig.java
package com.bankit.loan.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Database configuration and connection management
 * Handles database connection lifecycle and initialization
 */
public class DatabaseConfig {
    private static final int MAX_POOL_SIZE = 10;
    private static final Queue<Connection> connectionPool = new ConcurrentLinkedQueue<>();
    private static final Object lock = new Object();
    private static boolean isInitialized = false;

    /**
     * Gets a connection from the pool or creates a new one if needed
     */
    public static synchronized Connection getConnection() throws SQLException {
        Connection conn = connectionPool.poll();

        if (conn == null || conn.isClosed()) {
            conn = createNewConnection();
            if (!isInitialized) {
                initializeDatabase(conn);
                isInitialized = true;
            }
        }

        return conn;
    }

    /**
     * Creates a new database connection with proper settings
     */
    private static Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(AppConfig.getDatabaseUrl());
        conn.setAutoCommit(true);

        // Enable WAL mode and set busy timeout for better concurrent access
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL");
            stmt.execute("PRAGMA busy_timeout=30000");
            stmt.execute("PRAGMA foreign_keys=ON");
        }

        return conn;
    }

    /**
     * Initializes the database schema if it doesn't exist
     */
    private static void initializeDatabase(Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement()) {
            // Create Officers table if not exists
            statement.execute("""
                CREATE TABLE IF NOT EXISTS officers (
                    officer_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    contact TEXT NOT NULL
                )
            """);

            // Create Loans table if not exists
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
     * Returns a connection to the pool
     */
    public static void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed() && connectionPool.size() < MAX_POOL_SIZE) {
                    connectionPool.offer(conn);
                } else {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes all connections in the pool
     */
    public static synchronized void closeAllConnections() {
        Connection conn;
        while ((conn = connectionPool.poll()) != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        isInitialized = false;
    }

    /**
     * Begins a transaction on a specific connection
     */
    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.setAutoCommit(false);
        }
    }

    /**
     * Commits a transaction on a specific connection
     */
    public static void commitTransaction(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    /**
     * Rolls back a transaction on a specific connection
     */
    public static void rollbackTransaction(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.rollback();
            conn.setAutoCommit(true);
        }
    }
}