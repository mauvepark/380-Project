package edu.ucalgary.oop;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Singleton class to manage the database connection. 
 * This class loads the database configuration from a properties file and provides methods to get and close the connection.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection dbConnect;

    private String url;
    private String user;
    private String password;

    // constructor
    private DatabaseManager() {
        loadConfig();
        createConnection();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Loads the database configuration from the DatabaseCreds.properties file.
     * 
     * @throws RuntimeException if the configuration file is missing or cannot be loaded, or if required properties are missing.
     */
    private void loadConfig() {
        Properties prop = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("DatabaseCreds.properties")) {
            prop.load(input);

            this.url = prop.getProperty("url");
            this.user = prop.getProperty("user");
            this.password = prop.getProperty("password");

            if (url == null || user == null || password == null) {
                throw new RuntimeException("Missing database configuration values");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load database config", e);
        }
    }

    /**
     * Creates a new database connection using the loaded configuration values. This method is called during initialization and can also be called to re-establish a connection if it has been closed.
     * 
     * @throws RuntimeException if there is an error connecting to the database.
     */
    private void createConnection() {
        try {
            dbConnect = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            throw new RuntimeException("Could not connect to database.", e);
        }
    }

    /**
     * Returns the current database connection. If the connection is closed or null, a new connection will be created before returning.
     * 
     * @return The active database connection. An exception is thrown if there is an error creating a new connection when needed.
     * @throws RuntimeException if there is an error getting the database connection.
     */
    public Connection getConnection() {
        try {
            if (dbConnect == null || dbConnect.isClosed()) {
                createConnection();
            }
            return dbConnect;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection.", e);
        }
    }

    /**
     * Closes the database connection. This should be called when the application is shutting down to free resources.
     * 
     * @throws RuntimeException if there is an error closing the connection.
     */
    public void close() {
        try {
            if (dbConnect != null && !dbConnect.isClosed()) {
                dbConnect.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close connection.", e);
        }
    }
}