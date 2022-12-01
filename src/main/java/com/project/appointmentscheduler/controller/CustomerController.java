package com.project.appointmentscheduler.controller;

import com.project.appointmentscheduler.dao.AppointmentsDAO;
import com.project.appointmentscheduler.dao.CustomerDAO;
import com.project.appointmentscheduler.model.Appointment;
import com.project.appointmentscheduler.model.Customer;
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
 * Controls interactions between Customer class objects and the Customers FXML window.
 */
public class CustomerController implements Initializable {
    public TableView<Customer> allCustomers;
    public TableColumn customerIDCol;
    public TableColumn nameCol;
    public TableColumn addressCol;
    public TableColumn postalCodeCol;
    public TableColumn phoneNumberCol;
    public TableColumn countryCol;
    public TableColumn divisionCol;
    private Customer selected = null;
    private static Customer selectedCustomer = null;
    private ObservableList<Customer> allCustomersList = FXCollections.observableArrayList();

    /**
     * Displays all customers in the table and adds change listener to watch for selected customers in table.
     * Assigns selected customer to the customer associated with the last row clicked.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateCustomerTable();

        // Listen for table selection
        allCustomers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> setSelected(newSelection));
    }

    /**
     * Returns the customer that is currently selected
     * @return the customer that is currently selected
     */
    public static Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    /**
     * Deselects the currently selected customer
     */
    public static void clearSelectedCustomer () {
        selectedCustomer = null;
    }

    /**
     * Redirects user to New Customer window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onNew(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toNewCustomer(actionEvent);
    }

    /**
     * Redirects user to Update Customer window if a customer is selected.
     * An alert is displayed if no customer is selected.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onUpdate(ActionEvent actionEvent) throws IOException {
        if (selectedCustomer == null) {
            Alert notSelected = new Alert(Alert.AlertType.ERROR, "No customer selected. Please choose a " +
                    "customer from the list to proceed with update.");
            notSelected.showAndWait();
        } else {
            WindowNavigationController.toUpdateCustomer(actionEvent);
        }
    }

    /**
     * Deletes selected customer if there aren't any appointments assigned to the customer.
     * Presents warnings if no customer is selected, the deletion doesn't change any rows in database, or the customer
     * still has appointments.
     * @param actionEvent   the action that triggered the button
     * @throws SQLException if a SQL exception occurs
     */
    public void onDelete(ActionEvent actionEvent) throws SQLException {
        // Display error alert if no customer is selected
        if (selectedCustomer == null) {
            Alert notSelected = new Alert(Alert.AlertType.ERROR, "No customer selected.\n" +
                    "Please choose a customer from the list to proceed with deletion.");
            notSelected.showAndWait();
        } else {
            // Retrieve all appointments for selected customer
            ObservableList<Appointment> appointmentsList = AppointmentsDAO.getAppointmentsByCustomer(selectedCustomer.getCustomerID());
            if (appointmentsList.isEmpty()) {
                Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this customer?");
                Optional<ButtonType> result = confirmDelete.showAndWait();
                if (result.get() == ButtonType.OK) {
                    int customersDeleted = CustomerDAO.deleteCustomer(selectedCustomer.getCustomerID());
                    if (customersDeleted > 0) {
                        Alert customerDeleted = new Alert(Alert.AlertType.WARNING, selectedCustomer.getName() + " was removed.");
                        customerDeleted.show();

                        // Refresh current tableview
                        populateCustomerTable();
                    } else {
                        // Display error message if no rows were updated in the database
                        Alert customerNotDeleted = new Alert(Alert.AlertType.ERROR, "Unable to delete " + selectedCustomer.getName() +
                                "( #" + selectedCustomer.getCustomerID() + ").");
                        customerNotDeleted.showAndWait();
                    }
                }
            } else {
                // Display error if customer still has appointments and deny deletion request
                Alert customerHasAppointments = new Alert(Alert.AlertType.WARNING);
                customerHasAppointments.setContentText("Unable to delete customer.\n" +
                        "Please cancel all appointments for " + selectedCustomer.getName() + " (#" +
                        selectedCustomer.getCustomerID() + ") and try again.");
                customerHasAppointments.showAndWait();
            }
        }
    }

    /**
     * Assigns the given customer as the selected customer
     * @param customer the customer to be assigned as the selected customer
     */
    private void setSelected (Customer customer) {
        selectedCustomer = customer;
    }

    /**
     * Display all customers from the database in the customer table.
     */
    private void populateCustomerTable() {
        try {
            allCustomersList = CustomerDAO.getAllCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        allCustomers.setItems(allCustomersList);
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        divisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

    }

    /**
     * Redirects user to Main Menu.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onBack(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toMain(actionEvent);
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
