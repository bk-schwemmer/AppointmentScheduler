package com.project.appointmentscheduler.dao;

import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Generic statement operations for CRUD operations in the database
 */
public abstract class Operation {
    private static ResultSet result = null;
    private static int affectedRows = 0;

    /**
     * Generates a statement and sends the given inquiry string to the database as a CRUD operation.
     * @param inquiry a string with the SQL statement to be sent to the database
     */
    public static void performOperation (String inquiry) {
        String sql = inquiry;
        try {
            Statement stmnt = JDBC.connection.createStatement();
            if (sql.toLowerCase().startsWith("select")) {
                result = stmnt.executeQuery(sql);
            }
            if (sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("update") ||
                    sql.toLowerCase().startsWith("delete")) {
                affectedRows = stmnt.executeUpdate(sql);
            }
        } catch (Exception e){
            Alert sqlError = new Alert(Alert.AlertType.ERROR, "Error:" + e.getMessage());
            sqlError.showAndWait();
        }
    }

    /**
     * Returns the ResultSet of the operation performed from the performOperation function
     * @return the ResultSet of the previous operation
     */
    public static ResultSet getResult () {
        return result;
    }

    /**
     * Returns the number of affected rows of the operation performed from the performOperation function
     * @return the number of affected rows of the previous operation
     */
    public static int getAffectedRows () {
        return affectedRows;
    }
}
