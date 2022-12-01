package com.project.appointmentscheduler.controller;

import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Controls interactions between Main Menu window and items in the model and DAO
 */
public class MainMenuController {

    /**
     * Redirects user to Appointments window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void toAppointments(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toAppointment(actionEvent);
    }

    /**
     * Redirects user to Customers window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void toCustomers(ActionEvent actionEvent) throws IOException {
        WindowNavigationController.toCustomer(actionEvent);
    }

    /**
     * Redirects user to Reports window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public void toReports(ActionEvent actionEvent) throws IOException {
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
