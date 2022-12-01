package com.project.appointmentscheduler.controller;

import com.project.appointmentscheduler.dao.*;
import com.project.appointmentscheduler.model.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls interactions between New Customer window and items in the model and DAO
 */
public class NewCustomerController implements Initializable {
    public TextField customerName;
    public TextField customerAddress;
    public TextField customerPostal;
    public TextField customerPhone;
    public ComboBox<Country> customerCountry;
    public ComboBox<Division> customerDivision;

    /**
     * Populates Country combo box and adds listener check for changes.
     * Populates division box based on country selection.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Populate Country Dropdown
            ObservableList<Country> allCountries = CountryDAO.getAllCountries();
            customerCountry.setItems(allCountries);
            customerCountry.setPromptText("Select a Country");

            // Listen for change to Country box
            customerCountry.valueProperty().addListener((obs,oldCountry, newCountry) -> {
                // Only fill the Division box if a country is selected
                if (newCountry != null) {
                    populateDivisions();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates division combo box based on country selected in country combo box.
     */
    private void populateDivisions() {
        int selectedCountryID = customerCountry.getSelectionModel().getSelectedItem().getCountryID();
        customerDivision.getSelectionModel().clearSelection();

        // Populate Country Dropdown
        try {
            ObservableList<Division> allDivisionsOfCountry = DivisionDAO.getDivisionByCountryID(selectedCountryID);
            customerDivision.setItems(allDivisionsOfCountry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears all text fields and selection boxes. Asks for verification before implementing.
     * @param actionEvent  the action that triggered the button
     */
    public void onReset(ActionEvent actionEvent) {
        Alert resetConfirmation = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to clear the form?");
        Optional<ButtonType> result = resetConfirmation.showAndWait();
        if (result.get() == ButtonType.OK) {
            customerName.clear();
            customerAddress.clear();
            customerPhone.clear();
            customerPostal.clear();
            customerCountry.getSelectionModel().clearSelection();
            customerDivision.getSelectionModel().clearSelection();
        }
    }

    /**
     * Extract all entered data and insert customer object into database.
     * Displays alert if any input field is invalid or if there is an error with database insertion.
     * @param actionEvent  the action that triggered the button
     */
    public void onSubmit(ActionEvent actionEvent) {
        Country selectedCountry;
        Division selectedDivision;
        Customer newCustomer;

        // Check for empty text fields
        if ((!customerName.getText().isEmpty()) && (!customerAddress.getText().isEmpty()) && (!customerPostal.getText().isEmpty())
                && (!customerPhone.getText().isEmpty())) {
            try {
                selectedCountry = customerCountry.getSelectionModel().getSelectedItem();
                selectedDivision = customerDivision.getSelectionModel().getSelectedItem();

                // Generate a new appointment object with the entered information
                newCustomer = new Customer(customerName.getText(), customerAddress.getText(), customerPostal.getText(),
                        customerPhone.getText(), selectedDivision.getDivisionID(), selectedCountry.getCountryID());

                // Try to add the customer to the database
                try {
                    int rowsAdded = CustomerDAO.addCustomer(newCustomer);
                    if (rowsAdded < 1) {
                        Alert notAdded = new Alert(Alert.AlertType.INFORMATION, "Customer was not added");
                        notAdded.showAndWait();
                    } else {
                        WindowNavigationController.toCustomer(actionEvent);
                    }
                // Catch exceptions from inserting customer into database
                } catch (IOException e) {
                    e.printStackTrace();
                }
            // Catch exception from unselected items
            } catch (NullPointerException e) {
                Alert nullFields = new Alert(Alert.AlertType.ERROR, "Please fill out all boxes before submitting");
                nullFields.showAndWait();
            }
        // Catch issues from empty text fields
        } else {
            Alert nullCustomer = new Alert(Alert.AlertType.ERROR,"Please fill out all fields before submitting");
            nullCustomer.showAndWait();
        }
    }

    /**
     * Redirects user to Customer window without saving any data.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onBack(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toCustomer(actionEvent);
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
