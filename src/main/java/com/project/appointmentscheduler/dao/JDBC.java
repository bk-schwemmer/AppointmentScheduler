package com.project.appointmentscheduler.dao;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Creates and terminates the connection between the program and the database
 */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?serverTimeZone = UTC"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection;  // Connection Interface

    /**
     * Opens connection to the SQL database with standardized username and password
     */
    public static void openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        } catch(Exception e) {
            Alert connectionIssue = new Alert(Alert.AlertType.ERROR,"Unable to connect to the database.\n" +
                    "Error:" + e.getMessage());
            connectionIssue.showAndWait();
        }
    }

    /**
     * Closes connection to the SQL database
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch(Exception e) {
            Alert disconnectionIssue = new Alert(Alert.AlertType.ERROR,"Unable to disconnect from the database.\n" +
                "Error:" + e.getMessage());
            disconnectionIssue.showAndWait();
        }
    }
}
