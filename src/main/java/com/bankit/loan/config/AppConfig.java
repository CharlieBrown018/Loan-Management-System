package com.bankit.loan.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Application configuration manager
 * Handles loading and accessing application configuration properties
 */
public class AppConfig {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "/config.properties";
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_PATH = "src/main/resources/db/loandb.sqlite";
    private static final String DB_DEFAULT_URL = "jdbc:sqlite:" + DB_PATH;

    static {
        loadProperties();
        ensureDatabaseExists();
    }

    /**
     * Loads configuration properties from the properties file
     * Falls back to default values if file is not found
     */
    private static void loadProperties() {
        try (InputStream input = AppConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                setDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("Failed to load configuration file: " + e.getMessage());
            setDefaultProperties();
        }
    }

    /**
     * Ensures database directory and file exist
     */
    private static void ensureDatabaseExists() {
        try {
            // Create database directory if it doesn't exist
            Path dbDir = Paths.get("src/main/resources/db");
            Files.createDirectories(dbDir);

            // Create database file if it doesn't exist
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database file: " + e.getMessage(), e);
        }
    }

    /**
     * Sets default configuration properties
     */
    private static void setDefaultProperties() {
        properties.setProperty(DB_URL_KEY, DB_DEFAULT_URL);
    }

    /**
     * Gets the database URL from configuration
     * @return the configured database URL or default SQLite URL
     */
    public static String getDatabaseUrl() {
        return properties.getProperty(DB_URL_KEY, DB_DEFAULT_URL);
    }

    /**
     * Gets a configuration property value
     * @param key the property key
     * @param defaultValue the default value if property is not found
     * @return the property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}