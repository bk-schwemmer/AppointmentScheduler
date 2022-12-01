package com.project.appointmentscheduler.controller;

import com.project.appointmentscheduler.dao.AppointmentsDAO;
import com.project.appointmentscheduler.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Month;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Controls interactions between the Types by Month Report window and items in the model and DAO
 */
public class TypeReportController implements Initializable {
    public ComboBox<Month> month;
    public ComboBox<String> type;
    public Label appointmentCount;
    private Month selectedMonth = null;

    /**
     * Populates month dropdown box and sets listeners for changes to the month and type combo boxes.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Clear text of the appointment sum label
        appointmentCount.setText("");
        try {
            // Populate Available Months Dropdown
            ObservableList<Month> allMonths = getAllAppointmentMonths();
            month.setItems(allMonths);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add listener for Month selection
        month.valueProperty().addListener((obs, oldMonth, newMonth) -> {
            selectedMonth = newMonth;
            // Only fill the Type list if there is a month selected
            if (newMonth != null) {
                try {
                    populateTypes(getUniqueAppointmentTypes(getAllAppointmentTypesByMonth(newMonth)));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Add listener for Type selection
        type.valueProperty().addListener((obs,oldType, newType) -> {
            // Fills the appointment list based on the selected type
            if (newType != null) {
                try {
                    populateAppointmentCount(getAllAppointmentTypesByMonth(selectedMonth),newType);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Redirects user to Reports window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onBack(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toReport(actionEvent);
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
     * Calculates a complete and unique list of months that all appointments fall within.
     * @return a unique set of months that all appointments occur in.
     * @throws SQLException if a SQL exception occurs
     */
    private ObservableList<Month> getAllAppointmentMonths() throws SQLException {
        ObservableList<Appointment> allAppointments = AppointmentsDAO.getAllAppointments();
        ObservableList<Month> monthList = FXCollections.observableArrayList();
        for (int i = 0; i < allAppointments.size(); i++) {
            monthList.add(allAppointments.get(i).getStartTime().getMonth());
        }

        // Found HashSet idea and implementation at https://www.geeksforgeeks.org/get-unique-values-from-arraylist-in-java/
        HashSet<Month> monthSet = new HashSet<Month>(monthList);

        // Start list of months over with unique items only
        monthList.clear();
        Iterator<Month> iter = monthSet.iterator();
        while (iter.hasNext()) {
            monthList.add(iter.next());
        }

        return monthList;
    }

    /**
     * Calculates all appointment types that occur within a given month.
     * @param  selectedMonth the month to find all appointment types for
     * @return the list of unique appointment types for the given month
     * @throws SQLException if a SQL exception occurs
     */
    private ObservableList<String> getAllAppointmentTypesByMonth(Month selectedMonth) throws SQLException {
        ObservableList<Appointment> allAppointments = AppointmentsDAO.getAllAppointments();
        ObservableList<String> typeList = FXCollections.observableArrayList();

        for (int i = 0; i < allAppointments.size(); i++) {
            if (allAppointments.get(i).getStartTime().getMonth().equals(selectedMonth)) {
                typeList.add(allAppointments.get(i).getType());
            }
        }

        return typeList;
    }

    /**
     * Reduces list of appointment types down to a unique set
     * @param  types complete list of appointment types
     * @return a list of only unique types from the given list
     */
    private ObservableList<String> getUniqueAppointmentTypes(ObservableList<String> types) {
        // Found HashSet idea and implementation at https://www.geeksforgeeks.org/get-unique-values-from-arraylist-in-java/
        HashSet<String> typeSet = new HashSet<String>(types);
        ObservableList<String> uniqueTypes = FXCollections.observableArrayList();

        // Start list of types over with unique items only
        Iterator<String> iter = typeSet.iterator();
        while (iter.hasNext()) {
            uniqueTypes.add(iter.next());
        }
        return uniqueTypes;
    }

    /**
     * Populates the type combo box with the provided list
     * @param types list of types to populate combo box with.
     */
    private void populateTypes(ObservableList<String> types) {
        type.setItems(types);
        type.setPromptText("Select an Appointment Type");
    }

    /**
     * Tallies and displays the number of items of the selected type within the list of types.
     * @param types        list of all applicable appointment types
     * @param selectedType a string indicating the type that should be counted and displayed
     * @throws SQLException if a SQL exception occurs
     */
    private void populateAppointmentCount(ObservableList<String> types, String selectedType ) throws SQLException {
        int tally = 0;
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).equals(selectedType)) {
                tally++;
            }
        }

        // Display the results
        if (tally == 1) {
            appointmentCount.setText("There is\n" + tally + " \'" + selectedType + "\' appointment in\n" + selectedMonth);
        } else {
            appointmentCount.setText("There are\n" + tally + " \'" + selectedType + "\' appointments in\n" + selectedMonth);
        }
    }
}
