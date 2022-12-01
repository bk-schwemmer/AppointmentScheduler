package com.project.appointmentscheduler.controller;

import com.project.appointmentscheduler.dao.AppointmentsDAO;
import com.project.appointmentscheduler.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls interactions between Appointment window and items in the model and DAO
 */
public class AppointmentController implements Initializable {
    public TableColumn appointmentIDsCol;
    public TableColumn titlesCol;
    public TableColumn descriptionsCol;
    public TableColumn locationsCol;
    public TableColumn contactsCol;
    public TableColumn typesCol;
    public TableColumn startTimesCol;
    public TableColumn endTimesCol;
    public TableColumn customerIDsCol;
    public TableColumn userIDsCol;
    public TableView<Appointment> appointmentsTable;
    public RadioButton all;
    public RadioButton month;
    public RadioButton week;
    public ToggleGroup appointmentView;
    private static Appointment selectedAppointment = null;
    private Appointment selected = null;
    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

    /**
     * Sets appointment table to display all appointments. Adds event handler for appointment selected in table.
     * LAMBDA
     * A lambda expression is used as the event handler for appointment selection. A lambda is used here as shorthand
     * to set the selected appointment to eliminate the need for a small event handler method. This simplifies the code
     * and shortens it by using only the single line of necessary code to set the assigned appointment.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set default table view to all appointments
        onAllAppointmentSelection(new ActionEvent());

        // Listen for table selection
        appointmentsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> setSelected(newSelection));
    }

    /**
     * Displays all appointments from database in appointment table.
     * @param  actionEvent  the action that triggered the button
     */
    public void onAllAppointmentSelection(ActionEvent actionEvent) {
        try {
            appointmentsList = AppointmentsDAO.getAllAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showAppointmentsInTable();
    }

    /**
     * Displays appointments for current month in appointment table. The current month is the current calendar month.
     * @param  actionEvent  the action that triggered the button
     */
    public void onMonthAppointmentSelection(ActionEvent actionEvent) {
        try {
            appointmentsList = AppointmentsDAO.getAppointmentsFilteredBy("Month");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showAppointmentsInTable();
    }

    /**
     * Displays appointments for current week in appointment table. The current week is defined as today through
     * 7 days from now.
     * @param  actionEvent  the action that triggered the button
     */
    public void onWeekAppointmentSelection(ActionEvent actionEvent) {
        try {
            appointmentsList = AppointmentsDAO.getAppointmentsFilteredBy("Week");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showAppointmentsInTable();
    }

    /**
     * Redirects to the New Appointment window.
     * @param  actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onNew(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toNewAppointment(actionEvent);
    }

    /**
     * Redirects to the Update Appointment window if an appointment is selected in the table.
     * If no appointment is selected, an error message is shown.
     * @param  actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onUpdate(ActionEvent actionEvent) throws IOException {
        if (selected == null) {
            Alert notSelected = new Alert(Alert.AlertType.ERROR, "No appointment selected. Please choose an " +
                    "appointment from the list to proceed with update.");
            notSelected.showAndWait();
        } else {
            WindowNavigationController.toUpdateAppointment(actionEvent);
        }
    }

    /**
     * Deletes the appointment currently selected in the appointments table.
     * If no appointment is selected, there is an issue with the deletion, or the appointment cannot be deleted for
     * any reason, an alert error is thrown. Upon confirmation of successful deletion, a notification is given indicating
     * the appointment type and ID that was removed from the database.
     * @param  actionEvent  the action that triggered the button
     */
    public void onDelete(ActionEvent actionEvent) {
        // Verify an appointment was selected in the table
        if (selected == null) {
            Alert notSelected = new Alert(Alert.AlertType.ERROR, "No appointment selected.\n" +
                    "Please choose an appointment from the list to proceed with deletion.");
            notSelected.showAndWait();
        } else {
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this appointment?");
            Optional<ButtonType> result = confirmDelete.showAndWait();
            if (result.get() == ButtonType.OK) {
                int appointmentsDeleted = AppointmentsDAO.deleteAppointment(selected.getAppointmentID());
                // Error if no rows were actually changed in the database
                if (appointmentsDeleted > 0) {
                    Alert appointmentDeleted = new Alert(Alert.AlertType.WARNING, "The " + selected.getType() +
                            " appointment (#" + selected.getAppointmentID() + ") was removed.");
                    appointmentDeleted.show();

                    // Refresh current tableview
                    RadioButton selectedButton = (RadioButton) appointmentView.getSelectedToggle();
                    if (selectedButton.getId().equals("week")) {
                        onWeekAppointmentSelection(new ActionEvent());
                    } else if (selectedButton.getId().equals("month")) {
                        onMonthAppointmentSelection(new ActionEvent());
                    } else {
                        onAllAppointmentSelection(new ActionEvent());
                    }
                } else {
                    Alert appointmentNotDeleted = new Alert(Alert.AlertType.ERROR, "Unable to delete appointment (#" +
                            selected.getAppointmentID() + ").");
                    appointmentNotDeleted.showAndWait();
                }
            }
        }
    }

    /**
     * Provides the currently selected appointment to requester.
     * @return the Appointment that is currently selected
     */
    public static Appointment getSelectedAppointment() {
        return selectedAppointment;
    }

    /**
     * Deselects the currently selected appointment.
     */
    public static void clearSelectedAppointment() {
        selectedAppointment = null;
    }

    /**
     * Sets the selectedAppointment to the given appointment.
     * @param appointment the appointment to be assigned as the selected appointment
     */
    private void setSelected (Appointment appointment) {
        selected = appointment;
        selectedAppointment = selected;
    }

    /**
     * Enables the table to display the items from the backing list.
     */
    private void showAppointmentsInTable() {
        appointmentsTable.setItems(appointmentsList);
        appointmentIDsCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titlesCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionsCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationsCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactsCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        typesCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTimesCol.setCellValueFactory(new PropertyValueFactory<>("formattedStartTime"));
        endTimesCol.setCellValueFactory(new PropertyValueFactory<>("formattedEndTime"));
        customerIDsCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDsCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    /**
     * Redirects user to Main Menu.
     * @param  actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onBack(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toMain(actionEvent);
    }

    /**
     * Redirects user to Login window. Current user is unassigned.
     * @param  actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void signOut(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toLogin(actionEvent);
    }
}