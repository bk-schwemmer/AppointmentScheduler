package com.project.appointmentscheduler.dao;

import com.project.appointmentscheduler.controller.LoginWindowController;
import com.project.appointmentscheduler.model.Customer;
import com.project.appointmentscheduler.utilities.TimeConversions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Facilitates interactions between the Controllers and Customers in the database
 */
public class CustomerDAO {
    private static int customerID = 0;
    private static String name = null;
    private static String address = null;
    private static String postalCode = null;
    private static String phoneNumber = null;
    private static int divisionID = 0;

    /**
     * Retrieves the customer corresponding to the given id number.
     * @param id the id number of the customer to fetch
     * @return the customer corresponding to the given id number
     */
    public static Customer getCustomer(int id) {
        customerID = id;
        Customer customer = null;

        Operation.performOperation("SELECT * FROM customers WHERE Customer_ID = '" + customerID + "'");
        ResultSet customerSet = Operation.getResult();
        ObservableList<Customer> list = extractCustomers(customerSet);
        if (list.isEmpty()) {
            return null;
        } else {
            customer = list.get(0);
        }

        return customer;
    }

    /**
     * Retrieves all customers from the database
     * @return a list of all customers from the database
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<Customer> getAllCustomers() throws SQLException {
        Operation.performOperation("SELECT * FROM customers");
        ResultSet customerSet = Operation.getResult();

        return extractCustomers(customerSet);
    }

    /**
     * Inserts the provided customer into the database. Displays an error message if the insertion is unsuccessful.
     * @param newCustomer customer to add into the database
     * @return number of rows that were affected during the insertion attempt
     */
    public static int addCustomer(Customer newCustomer) {
        int rowsInserted = 0;
        name = newCustomer.getName();
        address = newCustomer.getAddress();
        postalCode = newCustomer.getPostalCode();
        phoneNumber = newCustomer.getPhoneNumber();
        String createdBy = LoginWindowController.getLoggedInUser().getUserName();
        divisionID = newCustomer.getDivisionID();

        // Time conversion to UTC
        LocalDateTime currentUTC = TimeConversions.convertLocalToUTC(LocalDateTime.now());

        String inq = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, " +
                "Last_Update, Last_Updated_By, Division_ID) VALUES ('" + name + "', '" + address + "', '" +
                postalCode  + "', '" + phoneNumber + "', '" + currentUTC + "', '" + createdBy + "', '" + currentUTC +
                "', '" + createdBy + "', " + divisionID + ")";

        Operation.performOperation(inq);

        rowsInserted = Operation.getAffectedRows();

        return rowsInserted;
    }

    /**
     * Overwrites the customer with given id with the provided customer in the database.
     * Displays and error message if the update is unsuccessful.
     * @param customerID customer id number to overwrite in the database
     * @param updatedCustomer new customer data to overwrite the existing customer with
     * @return number of rows that were affected during the update attempt
     */
    public static int updateCustomer(int customerID, Customer updatedCustomer) {
        int rowsUpdated = 0;
        name = updatedCustomer.getName();
        address = updatedCustomer.getAddress();
        postalCode = updatedCustomer.getPostalCode();
        phoneNumber = updatedCustomer.getPhoneNumber();
        String lastUpdatedBy = LoginWindowController.getLoggedInUser().getUserName();
        divisionID = updatedCustomer.getDivisionID();

        // Time conversion to UTC
        LocalDateTime currentUTC = TimeConversions.convertLocalToUTC(LocalDateTime.now());

        String inq = ("UPDATE customers " +
                " SET Customer_Name = '" + name + "', Address = '" + address + "', Postal_Code = '" + postalCode +
                "', Phone = '" + phoneNumber + "', Last_Update = '" + currentUTC + "', " + "Last_Updated_By = '" +
                lastUpdatedBy + "', Division_ID = " + divisionID +
                " WHERE Customer_ID = " + customerID);

        Operation.performOperation(inq);
        rowsUpdated = Operation.getAffectedRows();

        return rowsUpdated;
    }

    /**
     * Deletes the customer with given id from the database.
     * @param customerID customer id number to delete from the database
     * @return number of rows that were affected during the deletion attempt
     */
    public static int deleteCustomer(int customerID) {
        int rowsDeleted = 0;

        String inq = "DELETE FROM customers WHERE Customer_ID = " + customerID;
        Operation.performOperation(inq);
        rowsDeleted = Operation.getAffectedRows();

        return rowsDeleted;
    }

    /**
     * Extracts all customer data from the given ResultSet and inputs it into a new customer list
     * @param set the ResultSet to extract customer data from
     * @return a list of customers that were extracted
     */
    private static ObservableList<Customer> extractCustomers(ResultSet set) {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        try {
            while (set.next()) {
                try {
                    customerID = set.getInt("Customer_ID");
                    name = set.getString("Customer_Name");
                    address = set.getString("Address");
                    postalCode = set.getString("Postal_Code");
                    phoneNumber = set.getString("Phone");
                    divisionID = set.getInt("Division_ID");
                    int countryID = DivisionDAO.getDivision(divisionID).getCountryID();

                    customerList.add(new Customer(customerID, name, address, postalCode, phoneNumber, divisionID, countryID));
                } catch (NullPointerException ignore) {
                    Alert noCountry = new Alert(Alert.AlertType.ERROR, "There is no assigned country for this customer");
                    noCountry.showAndWait();
                }
            }
        } catch (SQLException ignore) {
            Alert sqlIssue = new Alert(Alert.AlertType.ERROR, "There was an issue with the extraction");
            sqlIssue.showAndWait();
        }

        return customerList;
    }

}
