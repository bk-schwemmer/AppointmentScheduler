package com.project.appointmentscheduler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controls navigation between fxml windows and items in the model and DAO
 */
public class WindowNavigationController {

    // STANDARD WINDOW METHODS
    /**
     * Redirects user to Login window. Current user is unassigned.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toLogin(ActionEvent actionEvent) throws IOException {
        LoginWindowController.clearLoggedInUser();

        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/LoginWindow.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Log In");
        stage.setScene(scene);
        stage.show();
    }


    // MAIN WINDOW NAVIGATORS
    /**
     * Redirects user to Main Menu.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toMain(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/MainMenu.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Redirects user to Customers window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toCustomer(ActionEvent actionEvent) throws IOException {
        CustomerController.clearSelectedCustomer();

        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/Customer.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,900, 600);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Redirects user to Appointments window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toAppointment(ActionEvent actionEvent) throws IOException {
        AppointmentController.clearSelectedAppointment();

        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/Appointment.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,1200, 600);
        stage.setTitle("Appointments");
        stage.setScene(scene);
        stage.show();
}

    /**
     * Redirects user to Reports window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toReport(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/Reports.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,400, 400);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();
    }


    // CUSTOMER SUB-WINDOWS
    /**
     * Redirects user to New Customer window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toNewCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/NewCustomer.fxml"));
        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 400, 500);
        stage.setTitle("New Customer");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Redirects user to Update Customer window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toUpdateCustomer(ActionEvent actionEvent) throws IOException {
            Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/UpdateCustomer.fxml"));
            Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 400, 500);
            stage.setTitle("Update Customer");
            stage.setScene(scene);
            stage.show();
        }


    // APPOINTMENT SUB-WINDOWS
    /**
     * Redirects user to New Appointment window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toNewAppointment(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load((WindowNavigationController.class.getResource("/view/NewAppointment.fxml")));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,400, 600);
        stage.setTitle("New Appointment");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Redirects user to Update Appointment window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toUpdateAppointment(ActionEvent actionEvent) throws IOException {
            Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/UpdateAppointment.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 400, 600);
            stage.setTitle("Update Appointment");
            stage.setScene(scene);
            stage.show();
    }


    // REPORTS SUB-WINDOWS
    /**
     * Redirects user to Appointment Type and Month Report window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toTypeReport(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/TypeReport.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,300, 300);
        stage.setTitle("Type Report");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Redirects user to Contact Schedule Report window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toContactReport(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/ContactSchedule.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,1000, 600);
        stage.setTitle("Contact Report");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Redirects user to Customers per Country Report window.
     * @param actionEvent  the action that triggered the button
     * @throws IOException if an input or output exception occurs
     */
    public static void toCustomerReport(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(WindowNavigationController.class.getResource("/view/CustomersPerCountry.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,300, 300);
        stage.setTitle("Customer Report");
        stage.setScene(scene);
        stage.show();
    }
}
