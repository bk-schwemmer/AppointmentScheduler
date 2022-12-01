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
 * Controls interactions between Update Customer window and items in the model and DAO
 */
public class UpdateCustomerController implements Initializable {
    public Label customerID;
    public TextField customerName;
    public TextField customerAddress;
    public TextField customerPostal;
    public TextField customerPhone;
    public ComboBox<Country> customerCountry;
    public ComboBox<Division> customerDivision;
    private Customer selectedCustomer;

    /**
     * Fills all text and selection boxes based on stored information for customer.
     * Adds listener to the division box to populate appropriate divisions based on the selected country.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedCustomer = CustomerController.getSelectedCustomer();
        // Display alert if no customer is selected
        if (selectedCustomer == null) {
            Alert selectionIssue = new Alert(Alert.AlertType.ERROR, "There was an error fetching the selected customer");
            selectionIssue.showAndWait();
        } else {
            int id = selectedCustomer.getCustomerID();
            String name = selectedCustomer.getName();
            String address = selectedCustomer.getAddress();
            String postal = selectedCustomer.getPostalCode();
            String phone = selectedCustomer.getPhoneNumber();

            // Populate Text Fields
            customerID.setText(String.valueOf(id));
            customerName.setText(name);
            customerAddress.setText(address);
            customerPostal.setText(postal);
            customerPhone.setText(phone);

            // Populate combo boxes
            try {
                // Populate Country Dropdown
                ObservableList<Country> allCountries = CountryDAO.getAllCountries();
                customerCountry.setItems(allCountries);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Select correct country
            customerCountry.getSelectionModel().selectFirst();
            while (customerCountry.getSelectionModel().getSelectedItem().getCountryID() != selectedCustomer.getCountryID()) {
                customerCountry.getSelectionModel().selectNext();
            }

            // Populate divisions box based on selected country and select the one assigned to this customer currently
            populateDivisions();
            customerDivision.getSelectionModel().selectFirst();
            while (customerDivision.getSelectionModel().getSelectedItem().getDivisionID() != selectedCustomer.getDivisionID()) {
                customerDivision.getSelectionModel().selectNext();
            }

            // Listen for change to Country box
            customerCountry.valueProperty().addListener((obs,oldCountry, newCountry) -> {
                // Only fill the Division box if a country is selected
                if (newCountry != null) {
                    populateDivisions();
                }
            });
        }
    }

    /**
     * Clears all selection and text from all customer fields
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
     * Updates the existing customer record in the database with the new information entered in the form.
     * Displays error if data entry is incomplete or if there is an issue updating the record in the database.
     * @param actionEvent the action that triggered the button
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
                    int rowsUpdated = CustomerDAO.updateCustomer(selectedCustomer.getCustomerID(), newCustomer);
                    if (rowsUpdated < 1) {
                        Alert notUpdated = new Alert(Alert.AlertType.INFORMATION, "Customer was not Updated");
                        notUpdated.showAndWait();
                    } else {
                        WindowNavigationController.toCustomer(actionEvent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
     * Redirects user to Customers window without saving data.
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

    /**
     * Populates the division combo box based on which country is selected
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
}
