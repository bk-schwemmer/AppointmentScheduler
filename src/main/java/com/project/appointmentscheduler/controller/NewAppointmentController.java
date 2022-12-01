package com.project.appointmentscheduler.controller;

import com.project.appointmentscheduler.dao.AppointmentsDAO;
import com.project.appointmentscheduler.dao.ContactDAO;
import com.project.appointmentscheduler.dao.CustomerDAO;
import com.project.appointmentscheduler.dao.UserDAO;
import com.project.appointmentscheduler.model.Appointment;
import com.project.appointmentscheduler.model.Contact;
import com.project.appointmentscheduler.model.Customer;
import com.project.appointmentscheduler.model.User;
import com.project.appointmentscheduler.utilities.TimeConversions;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls interactions between New Appointment window and items in the model and DAO
 */
public class NewAppointmentController implements Initializable {
    public TextField title;
    public TextField description;
    public TextField location;
    public TextField type;
    public ComboBox<Contact> contact;
    public DatePicker startDate;
    public DatePicker endDate;
    public ComboBox<LocalTime> startTime;
    public ComboBox<LocalTime> endTime;
    public ComboBox<Customer> customer;
    public ComboBox<User> user;

    // 10PM ET (Close of Business) converted to local time of user
    private final LocalTime lastAppointmentEndTime = TimeConversions.convertETToLocal(LocalDateTime.of(LocalDate.now(),LocalTime.of(20,00))).toLocalTime();

    /**
     * Populates all combo boxes and sets prompt text
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Populate Contact Dropdown
            ObservableList<Contact> allContacts = ContactDAO.getAllContacts();
            contact.setItems(allContacts);
            contact.setPromptText("Select a Contact . . .");

            // Populate Users Dropdown
            ObservableList<User> allUsers = UserDAO.getAllUsers();
            user.setItems(allUsers);
            user.setPromptText("Select a User . . .");

            // Populate Customer Dropdown
            ObservableList<Customer> allCustomers = CustomerDAO.getAllCustomers();
            customer.setItems(allCustomers);
            customer.setPromptText("Select a Customer . . .");

            // Populate Start Time selection box
            populateStartTimes();

            // Listen for change to Start Time box
            // Implementation found at https://stackoverflow.com/questions/26424769/javafx8-how-to-create-listener-for-selection-of-row-in-tableview
            startTime.valueProperty().addListener((obs,oldTime, newTime) -> {
                // Only fill the End Time box if there is a start time selected
                if (newTime != null) {
                    populateEndTimes();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills the Start Time box with time slots from 8AM to 10PM in 30 minute intervals
     */
    public void populateStartTimes() {
        // Convert 8AM ET to local time of user
        LocalTime appointmentStartTime_ET = LocalTime.of(8, 00);
        LocalDateTime startLDT_ET = LocalDateTime.of(LocalDate.now(),appointmentStartTime_ET);
        LocalDateTime startLDT_Local = TimeConversions.convertETToLocal(startLDT_ET);
        LocalTime appointmentStartTime_Local = startLDT_Local.toLocalTime();

        while (appointmentStartTime_Local.isBefore(lastAppointmentEndTime.minusMinutes(30).plusSeconds(1))) {
            startTime.getItems().add(appointmentStartTime_Local);
            appointmentStartTime_Local = appointmentStartTime_Local.plusMinutes(30);
        }
    }

    /**
     * Populates the End Time combo box with appropriate times based on start time.
     * First available end time is 30 minutes after start time
     */
    public void populateEndTimes() {
        // Save last End Time selection
        LocalTime previousEnd = null;
        if (!endTime.getSelectionModel().isEmpty()) {
            previousEnd = endTime.getSelectionModel().getSelectedItem();
        }
        // Clear whole End Time box
        endTime.getItems().clear();
        endTime.getSelectionModel().clearSelection();

        // Get beginning start and end times
        LocalTime selectedStartTime = startTime.getSelectionModel().getSelectedItem();
        LocalTime appointmentEndTime = selectedStartTime.plusMinutes(30);
        LocalTime firstAvailableEndTime = appointmentEndTime;

        // Fill End Time box with new time slots
        while (appointmentEndTime.isBefore(lastAppointmentEndTime.plusSeconds(1))) {
            endTime.getItems().add(appointmentEndTime);
            appointmentEndTime = appointmentEndTime.plusMinutes(30);
        }
        // Select first available end time as default
        endTime.getSelectionModel().selectFirst();

        // Compare last used end time and the new first available one, use whichever is later
        if ((previousEnd != null) && (previousEnd.isAfter(firstAvailableEndTime))) {
            while (!(endTime.getSelectionModel().getSelectedItem().equals(previousEnd))) {
                endTime.getSelectionModel().selectNext();
            }
        }
    }

    /**
     * Clear all text fields and selection boxes. Asks for verification before implementing.
     * @param actionEvent the action that triggered the button
     */
    public void onReset(ActionEvent actionEvent) {
        Alert resetConfirmation = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to clear the form?");
        Optional<ButtonType> result = resetConfirmation.showAndWait();
        if (result.get() == ButtonType.OK) {
            title.clear();
            description.clear();
            location.clear();
            contact.getSelectionModel().clearSelection();
            type.clear();
            startDate.getEditor().clear();
            startTime.getSelectionModel().clearSelection();
            endDate.getEditor().clear();
            endTime.getSelectionModel().clearSelection();
            customer.getSelectionModel().clearSelection();
            user.getSelectionModel().clearSelection();
        }
    }

    /**
     * Extract all entered data and insert appointment object into database.
     * Displays alert if any text field or combo box hasn't been filled out or if there is an appointment collision.
     * @param actionEvent the action that triggered the button
     */
    public void onSubmit(ActionEvent actionEvent) {
        Contact selectedContact;
        User selectedUser;
        Customer selectedCustomer;
        LocalTime selectedStartTime, selectedEndTime;
        LocalDate selectedStartDate, selectedEndDate;
        Appointment newAppointment;

        // Extract and convert all entered data into objects of proper class
        if ((!title.getText().isEmpty()) && (!description.getText().isEmpty()) && (!location.getText().isEmpty())
                && (!type.getText().isEmpty())) {
                    try {
                        selectedContact = contact.getSelectionModel().getSelectedItem();
                        selectedCustomer = customer.getSelectionModel().getSelectedItem();
                        selectedUser = user.getSelectionModel().getSelectedItem();
                        selectedStartDate = startDate.getValue();
                        selectedEndDate = endDate.getValue();
                        selectedStartTime = startTime.getSelectionModel().getSelectedItem();
                        selectedEndTime = endTime.getSelectionModel().getSelectedItem();

                    // Test for appointment collisions
                    try {
                        if (AppointmentsDAO.collisionWithStart(LocalDateTime.of(selectedStartDate, selectedStartTime))) {
                            Alert startCollision = new Alert(Alert.AlertType.ERROR, "The selected starting time overlaps with an existing appointment.\n" +
                                    "Please choose a different starting time.");
                            startCollision.showAndWait();

                        } else if (AppointmentsDAO.collisionWithEnd(LocalDateTime.of(selectedEndDate, selectedEndTime))) {
                            Alert endCollision = new Alert(Alert.AlertType.ERROR, "The selected ending time overlaps with an existing appointment.\n" +
                                    "Please choose a different ending time.");
                            endCollision.showAndWait();
                        } else if (AppointmentsDAO.collisionWithInteriorAppointment(LocalDateTime.of(selectedStartDate, selectedStartTime),
                                LocalDateTime.of(selectedEndDate, selectedEndTime))) {
                            Alert endCollision = new Alert(Alert.AlertType.ERROR, "The selected ending time overlaps with an existing appointment.\n" +
                                    "Please choose a different start or end time.");
                            endCollision.showAndWait();
                        } else {
                            // Generate a new appointment object with the entered information
                            newAppointment = new Appointment(title.getText(), description.getText(), location.getText(),
                                    selectedContact.getId(), type.getText(), LocalDateTime.of(selectedStartDate, selectedStartTime),
                                    LocalDateTime.of(selectedEndDate, selectedEndTime), selectedCustomer.getCustomerID(), selectedUser.getID());

                            // Try to add the appointment to the database
                            try {
                                int rowsUpdated = AppointmentsDAO.addAppointment(newAppointment);
                                if (rowsUpdated < 1) {
                                    Alert notUpdated = new Alert(Alert.AlertType.INFORMATION, "Appointment was not Added");
                                    notUpdated.showAndWait();
                                } else {
                                    WindowNavigationController.toAppointment(actionEvent);
                                }
                            // Catch exceptions from updating database
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // Catch exception from collision tests
                    } catch (SQLException e) {
                            Alert somethingIsNull = new Alert(Alert.AlertType.ERROR, "Please fill out all boxes before submitting");
                            somethingIsNull.showAndWait();
                        }
                    // Catch exception from empty selection / field
                    } catch (NullPointerException e) {
                        Alert nullFields = new Alert(Alert.AlertType.ERROR, "Please fill out all boxes before submitting");
                        nullFields.showAndWait();
                    }
        } else {
            Alert missingTextFields = new Alert(Alert.AlertType.ERROR, "Please fill out all fields before submitting");
            missingTextFields.showAndWait();
        }
    }

    /**
     * Redirects user to Appointment window without submitting any data.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onBack(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toAppointment(actionEvent);
    }

    /**
     * Redirects user to Login window. Current user is unassigned.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void signOut(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toLogin(actionEvent);
    }
}
