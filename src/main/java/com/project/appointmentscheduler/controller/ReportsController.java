package com.project.appointmentscheduler.controller;

import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Controls interactions between Reports window and items in the model and DAO
 */
public class ReportsController {

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

    /**
     * Redirects user to Appointment Type and Month Report window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onTypeReport(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toTypeReport(actionEvent);
    }

    /**
     * Redirects user to Contact Schedule Report window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onContactSchedule(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toContactReport(actionEvent);
    }

    /**
     * Redirects user to Customers per Country Report window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void onCustomersPerCountry(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toCustomerReport(actionEvent);
    }
}
