package com.bankit.loan.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application configuration manager
 * Handles loading and accessing application configuration properties
 */
public class AppConfig {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "/config.properties";
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_DEFAULT_URL = "jdbc:sqlite:src/main/resources/db/loandb.sqlite";

    static {
        loadProperties();
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
                // Set default values if config file is not found
                setDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("Failed to load configuration file: " + e.getMessage());
            setDefaultProperties();
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