package com.sms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager using JDBC.
 * Provides connections to the school_management MySQL database.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/school_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";  // Change this to your MySQL root password
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns a connection to the database.
     * Creates a new connection if the current one is closed or null.
     */
    public static Connection getConnection() throws SQLException {
        DatabaseConnection db = getInstance();
        if (db.connection == null || db.connection.isClosed()) {
            db.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return db.connection;
    }

    /**
     * Returns a new independent connection (for concurrent operations).
     */
    public static Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Closes the current connection.
     */
    public static void closeConnection() {
        DatabaseConnection db = getInstance();
        if (db.connection != null) {
            try {
                db.connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Tests the database connection.
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}

