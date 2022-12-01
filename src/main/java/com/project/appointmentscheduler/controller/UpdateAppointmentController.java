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
 * Controls interactions between Update Appointment window and items in the model and DAO
 */
public class UpdateAppointmentController implements Initializable {
    public Label appointmentID;
    public TextField title;
    public TextField description;
    public TextField location;
    public TextField type;
    public ComboBox<Contact> contact;
    public DatePicker startDate;
    public ComboBox<LocalTime> startTime;
    public DatePicker endDate;
    public ComboBox<LocalTime> endTime;
    public ComboBox<Customer> customer;
    public ComboBox<User> user;
    private Appointment selectedAppointment;

    // 10PM ET (End of Business) converted to local time of user
    private final LocalTime lastAppointmentEndTime = TimeConversions.convertETToLocal(LocalDateTime.of(LocalDate.now(),LocalTime.of(20,00))).toLocalTime();

    /**
     * Populate all fields with data for the selected appointment in the database.
     * If there was an issue assigning the appointment chosen from the Appointment window and error will be shown.
     * Adds listener to start time combo box to update end time box based on the selection.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedAppointment = AppointmentController.getSelectedAppointment();
        // Display alert if no appointment is selected
        if (selectedAppointment == null) {
            Alert selectionIssue = new Alert(Alert.AlertType.ERROR, "There was an error fetching the selected appointment");
            selectionIssue.showAndWait();
        } else {
            // Populate combo boxes
            try {
                // Populate Contact Dropdown
                ObservableList<Contact> allContacts = ContactDAO.getAllContacts();
                contact.setItems(allContacts);

                // Populate Users Dropdown
                ObservableList<User> allUsers = UserDAO.getAllUsers();
                user.setItems(allUsers);

                // Populate Customer Dropdown
                ObservableList<Customer> allCustomers = CustomerDAO.getAllCustomers();
                customer.setItems(allCustomers);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Select all fields based on database data
            appointmentID.setText(String.valueOf(selectedAppointment.getAppointmentID()));
            title.setText(selectedAppointment.getTitle());
            description.setText(selectedAppointment.getDescription());
            location.setText(selectedAppointment.getLocation());
            type.setText(selectedAppointment.getType());
            startDate.setValue(selectedAppointment.getStartTime().toLocalDate());
            endDate.setValue(selectedAppointment.getEndTime().toLocalDate());

            // Contact selection
            contact.getSelectionModel().selectFirst();
            while (contact.getSelectionModel().getSelectedItem().getId() != selectedAppointment.getContactID()) {
                contact.getSelectionModel().selectNext();
            }

            // User selection
            user.getSelectionModel().selectFirst();
            while (user.getSelectionModel().getSelectedItem().getID() != selectedAppointment.getUserID()) {
                user.getSelectionModel().selectNext();
            }

            // Customer selection
            customer.getSelectionModel().selectFirst();
            while (customer.getSelectionModel().getSelectedItem().getCustomerID() != selectedAppointment.getCustomerID()) {
                customer.getSelectionModel().selectNext();
            }

            // Populate Start Time and End Time boxes based on selected appointment info from database
            populateStartTimes();
            startTime.getSelectionModel().selectFirst();
            while (!(startTime.getSelectionModel().getSelectedItem().equals(selectedAppointment.getStartTime().toLocalTime()))) {
                startTime.getSelectionModel().selectNext();
            }
            populateEndTimes(selectedAppointment.getStartTime().toLocalTime());
            while (!(endTime.getSelectionModel().getSelectedItem().equals(selectedAppointment.getEndTime().toLocalTime()))) {
                endTime.getSelectionModel().selectNext();
            }

            // Listen for change to Start Time box
            // Implementation found at https://stackoverflow.com/questions/26424769/javafx8-how-to-create-listener-for-selection-of-row-in-tableview
            startTime.valueProperty().addListener((obs, oldTime, newTime) -> {
                // Only fill the End Time box if there is a start time selected
                if (newTime != null) {
                    populateEndTimes(newTime);
                }
            });
        }
    }

    /**
     * Clears all fields when hitting the Reset button. Asks for verification before processing
     * @param actionEvent  the action that triggered the button
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
            startDate.setValue(null);
            startTime.getSelectionModel().clearSelection();
            endDate.setValue(null);
            endTime.getSelectionModel().clearSelection();
            customer.getSelectionModel().clearSelection();
            user.getSelectionModel().clearSelection();
        }
    }

    /**
     * Extract all entered data and insert appointment object into database.
     * Displays alert if all items aren't filled, the database isn't successfully updated with the new data, or there
     * is a collision between the new proposed time and an existing appointment.
     * @param actionEvent the action that triggered the button
     */
    public void onSubmit(ActionEvent actionEvent) {
        Contact selectedContact;
        User selectedUser;
        LocalTime selectedStartTime;
        LocalTime selectedEndTime;
        Customer selectedCustomer;
        LocalDate selectedStartDate;
        LocalDate selectedEndDate;
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

                try {
                    if (AppointmentsDAO.collisionWithStart(LocalDateTime.of(selectedStartDate, selectedStartTime),
                            selectedAppointment.getAppointmentID())) {
                        Alert startCollision = new Alert(Alert.AlertType.ERROR, "The selected starting time overlaps with an existing appointment.\n" +
                                "Please choose a different starting time.");
                        startCollision.showAndWait();

                    } else if (AppointmentsDAO.collisionWithEnd(LocalDateTime.of(selectedEndDate, selectedEndTime),
                            selectedAppointment.getAppointmentID())) {
                        Alert endCollision = new Alert(Alert.AlertType.ERROR, "The selected ending time overlaps with an existing appointment.\n" +
                                "Please choose a different ending time.");
                        endCollision.showAndWait();
                    } else if (AppointmentsDAO.collisionWithInteriorAppointment(LocalDateTime.of(selectedStartDate, selectedStartTime),
                            LocalDateTime.of(selectedEndDate, selectedEndTime), selectedAppointment.getAppointmentID())) {
                        Alert interiorCollision = new Alert(Alert.AlertType.ERROR, "The selected ending time overlaps with an existing appointment.\n" +
                                "Please choose a different start or end time.");
                        interiorCollision.showAndWait();
                    } else {
                        // Generate a new appointment object with the entered information
                        newAppointment = new Appointment(title.getText(), description.getText(), location.getText(),
                                selectedContact.getId(), type.getText(), LocalDateTime.of(startDate.getValue(), selectedStartTime),
                                LocalDateTime.of(endDate.getValue(), selectedEndTime), selectedCustomer.getCustomerID(), selectedUser.getID());

                        // Try to add the appointment to the database
                        try {
                            int rowsUpdated = AppointmentsDAO.updateAppointment(selectedAppointment.getAppointmentID(), newAppointment);
                            if (rowsUpdated < 1) {
                                Alert notUpdated = new Alert(Alert.AlertType.INFORMATION, "Appointment was not Updated");
                                notUpdated.showAndWait();
                            } else {
                                // Successful update results in redirect to Appointment window
                                WindowNavigationController.toAppointment(actionEvent);
                            }
                        // Catch exception for failure to update database
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                // Catch exception for failure to create new appointment with entered data
                } catch (SQLException e) {
                    Alert somethingIsNull = new Alert(Alert.AlertType.ERROR, "Please fill out all boxes before submitting");
                    somethingIsNull.showAndWait();
                }
            // Catch exception for failure to extract entered data
            } catch (NullPointerException e) {
                Alert nullFields = new Alert(Alert.AlertType.ERROR, "Please fill out all boxes before submitting");
                nullFields.showAndWait();
            }
        // Issue with text fields
        } else {
            Alert missingTextFields = new Alert(Alert.AlertType.ERROR, "Please fill out all fields before submitting");
            missingTextFields.showAndWait();
        }
    }

    /**
     * Redirects user to Appointments window without saving any data.
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

    /**
     * Fills the Start Time box with time slots from 8AM to 10PM in 30 minute intervals
     */
    private void populateStartTimes() {
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
     * Populates End Time box with time slots from 30 minutes after the passed start time to 10PM in 30 minute intervals.
     * Compares the last selected end time and the first available time slot, selects whichever is later.
     * @param selectedStartTime the time to start with
     */
    private void populateEndTimes(LocalTime selectedStartTime) {
        // Save last End Time selection
        LocalTime previousEnd = null;
        if (!endTime.getSelectionModel().isEmpty()) {
            previousEnd = endTime.getSelectionModel().getSelectedItem();
        }

        // Clear whole End Time box
        endTime.getItems().clear();
        endTime.getSelectionModel().clearSelection();

        // Fill End Time box with new time slots
        LocalTime appointmentEndTime = selectedStartTime.plusMinutes(30);
        LocalTime firstAvailableEndTime = appointmentEndTime;
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
}
