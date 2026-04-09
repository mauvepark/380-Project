package edu.ucalgary.oop;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

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

    // singleton pattern
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // load databse configuration
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

    // create connection to database
    private void createConnection() {
        try {
            dbConnect = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            throw new RuntimeException("Could not connect to database.", e);
        }
    }

    public Connection getConnection() {
        if (dbConnect == null) {
            createConnection();
        }
        return dbConnect;
    }

    // close connection to database
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