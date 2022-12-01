package com.project.appointmentscheduler.controller;

import com.project.appointmentscheduler.dao.AppointmentsDAO;
import com.project.appointmentscheduler.dao.ContactDAO;
import com.project.appointmentscheduler.model.Appointment;
import com.project.appointmentscheduler.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controls interactions between the Contact Schedule Report FXML window and items from the model.
 */
public class ContactScheduleController implements Initializable {

    public TableColumn appointmentIDsCol;
    public TableColumn titlesCol;
    public TableColumn descriptionsCol;
    public TableColumn locationsCol;
    public TableColumn typesCol;
    public TableColumn startTimesCol;
    public TableColumn endTimesCol;
    public TableColumn customerIDsCol;
    public TableView<Appointment> appointmentsTable;
    public ComboBox<Contact> contact;
    public Label appointmentTotal;
    private Contact selected = null;
    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

    /**
     * Sets appointments table to display appointments for the first contact by default.
     * Adds event handler for appointment selected in table. Populates the label at the bottom with total appointments for
     * the selected contact.
     * LAMBDA
     * A lambda expression is used as the event handler for contact selection. A lambda is used here as shorthand
     * to check for changes to the contact combo box and update the appointments table list on a change. This lambda
     * expression takes advantage of the ChangeListener functional interface by using a block to define the steps required
     * once the listener detects a change. This eliminates the need for an anonymous methods or another helper function.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set default table view to be the first contact
        populateContactsList();

        // Listen for change to Start Time box
        contact.valueProperty().addListener((obs,oldContact, newContact) -> {
            // Only fill the End Time box if there is a start time selected
            if (newContact != null) {
                try {
                    appointmentsList = AppointmentsDAO.getAppointmentsByContact(newContact.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                showAppointmentsInTable();
                printAppointmentTotal(newContact.getName(), appointmentsList.size());
            }
        });

        // Select first contact by default
        contact.getSelectionModel().selectFirst();
    }

    /**
     * Displays all contacts from database in the Contacts' combo box
     */
    public void populateContactsList() {
        ObservableList<Contact> contactsList = null;
        try {
            contactsList = ContactDAO.getAllContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        contact.setItems(contactsList);
    }

    /**
     * Assigns properties from the backing list to populate the table columns
     */
    private void showAppointmentsInTable() {
        appointmentsTable.setItems(appointmentsList);
        appointmentIDsCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titlesCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionsCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationsCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        typesCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTimesCol.setCellValueFactory(new PropertyValueFactory<>("formattedStartTime"));
        endTimesCol.setCellValueFactory(new PropertyValueFactory<>("formattedEndTime"));
        customerIDsCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    /**
     * Populates the appointmentTotal label with specific appointment count and contact name.
     * @param name  a string for the contact name to populate appointment count for
     * @param count number of appointments for contact
     */
    private void printAppointmentTotal(String name, int count) {
        if (count == 1) {
            appointmentTotal.setText("There is " + count + " appointment for " + name);
        } else {
            appointmentTotal.setText("There are " + count + " appointments for " + name);
        }
    }

    /**
     * Redirects user to Reports window.
     * @param  actionEvent  the action that triggered the button
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
}