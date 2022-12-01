package com.project.appointmentscheduler.main;

import com.project.appointmentscheduler.dao.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Begins the application
 */
public class Main extends Application {
    /**
     * Begins the program at the login screen
     * @param stage the stage to present the windows in
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginWindow.fxml"));
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main Method - opens and closes the database connection
     * @param args CMI args
     * @throws SQLException if a SQL exception occurs
     */
    public static void main(String[] args) throws SQLException {
        JDBC.openConnection();

        launch();

        JDBC.closeConnection();
    }
}