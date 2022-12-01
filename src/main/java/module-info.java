module com.project.appointmentscheduler.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.project.appointmentscheduler.controller;
    opens com.project.appointmentscheduler.controller to javafx.fxml;
    exports com.project.appointmentscheduler.main;
    opens com.project.appointmentscheduler.main to javafx.fxml;
    exports com.project.appointmentscheduler.model;
    opens com.project.appointmentscheduler.model to javafx.fxml;
}