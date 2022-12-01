package com.project.appointmentscheduler.controller;

import com.project.appointmentscheduler.dao.AppointmentsDAO;
import com.project.appointmentscheduler.dao.FileDAO;
import com.project.appointmentscheduler.dao.UserDAO;
import com.project.appointmentscheduler.model.Appointment;
import com.project.appointmentscheduler.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controls interactions between Login window and items in the model and DAO
 */
public class LoginWindowController implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    public Label usernameLabel;
    public Label passwordLabel;
    public Button logInButton;
    public Label logInLabel;
    public Label zoneID;
    String usernameString = null;
    private static User loggedInUser = null;
    ResourceBundle languageBundle;

    /**
     * Displays current system zone ID and sets language resource bundle if system is set to French.
     * Translates the login window text and error messages to French when appropriate.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        zoneID.setText(ZoneId.systemDefault().toString());
         // Assigns appropriate resource bundle if system language is french
        if (Locale.getDefault().getLanguage().equals("fr")) {
            languageBundle = ResourceBundle.getBundle("com.project.appointmentscheduler.language",Locale.getDefault());
        }

        // Retrieve translated French versions of window text
        try {
            logInLabel.setText(languageBundle.getString("Log In"));
            usernameLabel.setText(languageBundle.getString("Username"));
            passwordLabel.setText(languageBundle.getString("Password"));
            logInButton.setText(languageBundle.getString("Log In"));
        } catch (Exception ignored){}
    }

    /**
     * Verifies the username and password match data for a user in the database.
     * Any invalid field will present applicable error message. Matching credentials redirect user to Main Menu window.
     * @param  actionEvent  the action that triggered the button
     * @throws IOException  if an input or output exception occurs
     * @throws SQLException if a SQL exception occurs
     */
    public void logIn(ActionEvent actionEvent) throws IOException, SQLException {
        usernameString = username.getText();
        String passwordString = password.getText();

        // Check for valid username
        User validUser = UserDAO.getUser(usernameString);
        if (validUser == null) {
            FileDAO.unsuccessfulLogin(usernameString);
            // Invalid Username presents error popup
            Alert usernameAlert = new Alert(Alert.AlertType.ERROR);
            try {
                usernameAlert.setTitle(languageBundle.getString("Invalid Username"));
                usernameAlert.setContentText(languageBundle.getString("Please enter a valid username"));
            } catch (Exception e) {
                usernameAlert.setTitle("Invalid Username");
                usernameAlert.setContentText("Please enter a valid username");
            }
            usernameAlert.showAndWait();
        } else {
            // Invalid Password presents error popup
            if (!(passwordString.equals(validUser.getPassword()))) {
                FileDAO.unsuccessfulLogin(usernameString);
                Alert passwordAlert = new Alert(Alert.AlertType.ERROR);
                try {
                    passwordAlert.setTitle(languageBundle.getString("Invalid Password"));
                    passwordAlert.setContentText(languageBundle.getString("Please enter a valid password"));
                } catch (Exception e) {
                    passwordAlert.setTitle("Invalid Password");
                    passwordAlert.setContentText("Please enter a valid password");
                }
                passwordAlert.showAndWait();

            // Valid Password redirects to Main Menu window
            } else {
                FileDAO.successfulLogin(usernameString);
                setLoggedInUser();

                ObservableList<Appointment> upcomingAppointmentsList = FXCollections.observableArrayList();
                try {
                    upcomingAppointmentsList = AppointmentsDAO.getUpcomingAppointmentsByUser(loggedInUser.getID());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Display Customer Alert Message depending on result of upcoming appointment query
                Alert upcomingAppointments = new Alert(Alert.AlertType.WARNING);
                if (upcomingAppointmentsList == null || upcomingAppointmentsList.isEmpty()) {
                    upcomingAppointments.setContentText("There are no appointments for you in the next 15 minutes");
                } else {
                    upcomingAppointments.setContentText("There is an appointment for you within the next 15 minutes: \n" +
                            "Appointment ID: " + upcomingAppointmentsList.get(0).getAppointmentID() + "\n" +
                            "Start Date: " + upcomingAppointmentsList.get(0).getStartTime().toLocalDate() + "\n" +
                            "Start Time: " + upcomingAppointmentsList.get(0).getStartTime().toLocalTime() + "\n" +
                            "User ID: " + loggedInUser.getID());

                }
                upcomingAppointments.showAndWait();
                WindowNavigationController.toMain(actionEvent);
            }
        }
    }

    /**
     * Returns the user that is currently logged in.
     * @return the user that is currently logged in
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Deselects user that is currently logged in
     */
    public static void clearLoggedInUser() {
        loggedInUser = null;
    }

    /**
     * Sets private variable indicating the user that is currently logged in
     */
    private void setLoggedInUser() {
        try {
            loggedInUser = UserDAO.getUser(usernameString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
