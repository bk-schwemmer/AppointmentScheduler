package com.project.appointmentscheduler.controller;

import com.project.appointmentscheduler.dao.CountryDAO;
import com.project.appointmentscheduler.dao.CustomerDAO;
import com.project.appointmentscheduler.model.Customer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controls interactions between Customers Per Country Report and items in the model and DAO
 */
public class CustomersPerCountryController implements Initializable {
    public Label topName;
    public Label midName;
    public Label bottomName;
    public Label topCount;
    public Label midCount;
    public Label bottomCount;
    private int countUS;
    private int countUK;
    private int countCanada;
    private int topIndex = 0;
    private int midIndex = 1;
    private int bottomIndex = 2;

    /**
     * Retrieves all customers from database, counts them, orders, and displays them.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countUS = 0;
        countUK = 0;
        countCanada = 0;
        try {
            ObservableList<Customer> customerList = CustomerDAO.getAllCustomers();
            int[] countryCount = countCustomersPerCountry(customerList);
            orderCountries(countryCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // HELPER FUNCTIONS
    /**
     * Totals the number of customers in each country and returns it in an array list
     * @param list list of customers to count
     * @return     an array of sums for customers in each country. Order is based on Country ID
     */
    private int[] countCustomersPerCountry(ObservableList<Customer> list) {
        // Counter for customers in each country
        for (Customer customer: list) {
            switch (customer.getCountryID()) {
                case 1: countUS++;
                    break;
                case 2: countUK++;
                    break;
                case 3: countCanada++;
                    break;
            }
        }
        return new int[]{countUS, countUK, countCanada};
    }

    /**
     * Calculates and displays proper order of customers per country.
     * @param count an array with the total appointments for each country
     */
    private void orderCountries(int[] count) {
        final int SUM_OF_INDEXES = 3;
        // Iterate list of country sums to determine highest and lowest customer counts
        for (int i = 0; i < count.length; i++) {
            if (count[i] > count[topIndex]) {
                topIndex = i;
            }
            if (count[i] < count[bottomIndex]) {
                bottomIndex = i;
            }
        }
        // Middle index country calculation
        int topAndBottomSum = topIndex + bottomIndex;
        int midIndex = SUM_OF_INDEXES - topAndBottomSum;

        // Populate the labels
        publishValues(CountryDAO.getCountry(topIndex + 1).getCountryName(),
                CountryDAO.getCountry(midIndex + 1).getCountryName(),
                CountryDAO.getCountry(bottomIndex + 1).getCountryName(),
                count[topIndex], count[midIndex], count[bottomIndex]);
    }

    /**
     * Populates labels indicating country names and corresponding customer counts
     * @param top     string name of the highest scoring country
     * @param mid     string name of the middle scoring country
     * @param bottom  string name of the lowest scoring country
     * @param topC    highest number of customers
     * @param midC    middle number of customers
     * @param bottomC fewest number of customers
     */
    private void publishValues(String top, String mid, String bottom, int topC, int midC, int bottomC) {
        topName.setText(top);
        midName.setText(mid);
        bottomName.setText(bottom);
        topCount.setText(topC + " customers");
        midCount.setText(midC + " customers");
        bottomCount.setText(bottomC + " customers");
    }
}
