package com.bankit.loan.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application configuration manager
 * Handles loading and accessing application configuration properties
 */
public class AppConfig {
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "/config.properties";
    private static final String DB_URL_KEY = "db.url";

    // Database configuration
    private static final String DB_PATH = "src/main/resources/db/loandb.db";
    private static final String DB_DEFAULT_URL = "jdbc:sqlite:" + new File(DB_PATH).getAbsolutePath();

    // Additional configuration keys
    private static final String APP_NAME_KEY = "app.name";
    private static final String APP_VERSION_KEY = "app.version";
    private static final String UI_THEME_KEY = "ui.theme";

    static {
        try {
            initialize();
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Failed to initialize application configuration", e);
            throw e;
        }
    }

    /**
     * Initializes the configuration system
     */
    private static void initialize() {
        loadProperties();
        ensureDatabaseExists();
        validateConfiguration();
    }

    /**
     * Loads configuration properties from the properties file
     * Falls back to default values if file is not found
     */
    private static void loadProperties() {
        try (InputStream input = AppConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                logger.info("Successfully loaded configuration from " + CONFIG_FILE);
            } else {
                logger.warning("Configuration file not found. Using default values.");
                setDefaultProperties();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load configuration file. Using default values.", e);
            setDefaultProperties();
        }
    }

    /**
     * Ensures database directory and file exist
     * Creates them if they don't exist
     */
    private static void ensureDatabaseExists() {
        try {
            // Ensure parent directories exist
            Path dbDir = Paths.get(DB_PATH).getParent();
            if (dbDir != null) {
                Files.createDirectories(dbDir);
                logger.info("Database directory created/verified at: " + dbDir);
            }

            // Create database file if it doesn't exist
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    logger.info("Created new database file at: " + DB_PATH);
                }
            } else {
                logger.info("Using existing database file at: " + DB_PATH);
            }

            // Verify file permissions
            if (!dbFile.canRead() || !dbFile.canWrite()) {
                throw new IOException("Insufficient permissions for database file");
            }
        } catch (IOException e) {
            String errorMsg = "Failed to initialize database file: " + e.getMessage();
            logger.log(Level.SEVERE, errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Sets default configuration properties
     */
    private static void setDefaultProperties() {
        properties.setProperty(DB_URL_KEY, DB_DEFAULT_URL);
        properties.setProperty(APP_NAME_KEY, "BankIT Loan Management System");
        properties.setProperty(APP_VERSION_KEY, "1.0.0");
        properties.setProperty(UI_THEME_KEY, "light");

        logger.info("Default properties have been set");
    }

    /**
     * Validates the configuration settings
     * Ensures all required properties are present and valid
     */
    private static void validateConfiguration() {
        // Validate database URL
        String dbUrl = getDatabaseUrl();
        if (!dbUrl.startsWith("jdbc:sqlite:")) {
            throw new IllegalStateException("Invalid database URL format: " + dbUrl);
        }

        // Validate other required properties
        validateRequiredProperty(APP_NAME_KEY);
        validateRequiredProperty(APP_VERSION_KEY);
        validateRequiredProperty(UI_THEME_KEY);

        logger.info("Configuration validation completed successfully");
    }

    /**
     * Validates that a required property exists
     *
     * @param key the property key to validate
     */
    private static void validateRequiredProperty(String key) {
        if (!properties.containsKey(key)) {
            String errorMsg = "Missing required configuration property: " + key;
            logger.severe(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    /**
     * Gets the database URL from configuration
     *
     * @return the configured database URL or default SQLite URL
     */
    public static String getDatabaseUrl() {
        return properties.getProperty(DB_URL_KEY, DB_DEFAULT_URL);
    }

    /**
     * Gets a configuration property value
     *
     * @param key the property key
     * @param defaultValue the default value if property is not found
     * @return the property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        if (value == null && defaultValue == null) {
            logger.warning("No value found for key: " + key + " and no default value provided");
        }
        return value;
    }

    /**
     * Gets the database file path
     *
     * @return the absolute path to the database file
     */
    public static String getDatabasePath() {
        return new File(DB_PATH).getAbsolutePath();
    }

    /**
     * Gets the application name
     *
     * @return the configured application name
     */
    public static String getApplicationName() {
        return getProperty(APP_NAME_KEY, "BankIT Loan Management System");
    }

    /**
     * Gets the application version
     *
     * @return the configured application version
     */
    public static String getApplicationVersion() {
        return getProperty(APP_VERSION_KEY, "1.0.0");
    }

    /**
     * Gets the UI theme
     *
     * @return the configured UI theme
     */
    public static String getUITheme() {
        return getProperty(UI_THEME_KEY, "light");
    }
}