package com.project.appointmentscheduler.dao;

import com.project.appointmentscheduler.controller.LoginWindowController;
import com.project.appointmentscheduler.model.Appointment;
import com.project.appointmentscheduler.utilities.TimeConversions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;

/**
 * Facilitates interactions between the Controllers and Appointments in the database
 */
public class AppointmentsDAO {
    private static int appointmentID = 0;
    private static String title = null;
    private static String description = null;
    private static String location = null;
    private static String type = null;
    private static LocalDateTime startTime = null;
    private static LocalDateTime endTime = null;
    private static String createdBy = null;
    private static String lastUpdatedBy = null;
    private static int customerID = 0;
    private static int userID = 0;
    private static int contactID = 0;

    /**
     * Retrieves the appointment corresponding to the given id number.
     * @param id the id number of the appointment to fetch
     * @return the appointment corresponding to the given id number
     */
    public static Appointment getAppointment(int id) {
        appointmentID = id;
        Appointment appointment = null;

        try {
            Operation.performOperation("SELECT * FROM appointments WHERE Appointment_ID = '" + appointmentID + "'");
            ResultSet appointmentSet = Operation.getResult();
            ObservableList<Appointment> list = extractAppointment(appointmentSet);
            if (list.isEmpty()) {
                return null;
            } else {
                appointment = list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointment;
    }

    /**
     * Retrieves all appointments corresponding to the given customer id number.
     * @param id the id number of the customer to fetch appointments for
     * @return a list of appointments for the given customer
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<Appointment> getAppointmentsByCustomer(int id) throws SQLException {
        Operation.performOperation("SELECT * FROM appointments WHERE Customer_ID = " + id);
        ResultSet appointmentSet = Operation.getResult();

        return extractAppointment(appointmentSet);
    }

    /**
     * Retrieves all appointments corresponding to the given contact id number.
     * @param id the id number of the contact to fetch appointments for
     * @return a list of appointments for the given contact
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<Appointment> getAppointmentsByContact(int id) throws SQLException {
        Operation.performOperation("SELECT * FROM appointments WHERE Contact_ID = " + id);
        ResultSet appointmentSet = Operation.getResult();

        return extractAppointment(appointmentSet);
    }

    /**
     * Retrieves all appointments for a specific timeframe (either current month or current week)
     * @param timeFrame string value indicating month or week
     * @return list of appointments for given timeframe
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<Appointment> getAppointmentsFilteredBy(String timeFrame) throws SQLException {
        LocalDateTime filteredStartUTC = null;
        LocalDateTime filteredEndUTC = null;

        if (timeFrame == "Month") {
            int currentDayOfMonth = LocalDate.now().getDayOfMonth();
            int currentMonthLength = LocalDate.now().lengthOfMonth();
            filteredStartUTC = TimeConversions.convertLocalToUTC(LocalDateTime.of(LocalDate.now().minusDays(currentDayOfMonth-1), LocalTime.of(00,01)));
            filteredEndUTC = TimeConversions.convertLocalToUTC(filteredStartUTC.plusDays(currentMonthLength).minusMinutes(2));

        } else if (timeFrame == "Week") {
            filteredStartUTC = TimeConversions.convertLocalToUTC(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 1)));
            filteredEndUTC = filteredStartUTC.plusDays(7).minusMinutes(2);
        }

        // Fetch appointments between selected timeframe
        String inq = ("SELECT * FROM appointments WHERE Start BETWEEN '" + Timestamp.valueOf(filteredStartUTC) + "' AND '" + Timestamp.valueOf(filteredEndUTC) + "'");
        Operation.performOperation(inq);

        ResultSet appointmentSet = Operation.getResult();

        return extractAppointment(appointmentSet);
    }

    /**
     * Retrieves all appointments within the next 15 minutes for the given user
     * @param id the id number of the user to fetch appointments for
     * @return a list of appointments starting in the next 15 minutes for the given user
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<Appointment> getUpcomingAppointmentsByUser(int id) throws SQLException {
        // Convert current time to UTC and set upcoming time window threshold
        final int MINUTES_THRESHOLD = 15;
        LocalDateTime nowUTC = TimeConversions.convertLocalToUTC(LocalDateTime.now());
        LocalDateTime thresholdUTC = nowUTC.plusMinutes(MINUTES_THRESHOLD);

        // Get applicable appointments from DB
        String inq = ("SELECT * FROM appointments WHERE User_ID = " + id +
                " AND Start BETWEEN '" + Timestamp.valueOf(nowUTC) + "' AND '" + Timestamp.valueOf(thresholdUTC) + "'");
        Operation.performOperation(inq);
        ResultSet appointmentSet = Operation.getResult();

        return extractAppointment(appointmentSet);
    }

    /**
     * Retrieves all appointments from the database
     * @return a list of all appointments from the database
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        Operation.performOperation("SELECT * FROM appointments");
        ResultSet appointmentSet = Operation.getResult();

        return extractAppointment(appointmentSet);
    }

    /**
     * Determines if there are any collisions between the provided start time and any appointments in the database
     * @param start a time to compare all existing appointment times to
     * @return true if the given time interferes with an existing appointment. false if there is no interference.
     * @throws SQLException if a SQL exception occurs
     */
    public static boolean collisionWithStart(LocalDateTime start) throws SQLException {
        return collisionWithStart(start,-1);
    }

    /**
     * Determines if there are any collisions between the provided start time and any appointments in the database,
     * excluding the provided appointment id
     * @param start                a time to compare all existing appointment times to
     * @param currentAppointmentID the id number of the appointment to exclude from the collision calculation
     * @return true if the given time interferes with an existing appointment. false if there is no interference.
     * @throws SQLException if a SQL exception occurs
     */
    public static boolean collisionWithStart(LocalDateTime start, int currentAppointmentID) throws SQLException {
        ObservableList<Appointment> allAppointments = getAllAppointments();
        boolean collision = false;

        for (int i = 0; i < allAppointments.size(); i++) {
            LocalDateTime appointmentStart = allAppointments.get(i).getStartTime();
            LocalDateTime appointmentEnd = allAppointments.get(i).getEndTime();

            // Exclude collisions with the current appointment
            if (allAppointments.get(i).getAppointmentID() != currentAppointmentID) {
                // Check for collisions with new prospective end time
                if (start.isEqual(appointmentStart) || (start.isAfter(appointmentStart) && start.isBefore(appointmentEnd))) {
                    collision = true;
                    break;
                }
            }
        }

        return collision;
    }

    /**
     * Determines if there are any collisions between the provided time and any appointments in the database
     * @param end a time to compare all existing appointment times to
     * @return true if the given time interferes with an existing appointment. false if there is no interference.
     * @throws SQLException if a SQL exception occurs
     */
    public static boolean collisionWithEnd(LocalDateTime end) throws SQLException {
        return collisionWithEnd(end,-1);
    }

    /**
     * Determines if there are any collisions between the provided end time and any appointments in the database,
     * excluding the provided appointment id
     * @param end                  a time to compare all existing appointment times to
     * @param currentAppointmentID the id number of the appointment to exclude from the collision calculation
     * @return true if the given time interferes with an existing appointment. false if there is no interference.
     * @throws SQLException if a SQL exception occurs
     */
    public static boolean collisionWithEnd(LocalDateTime end, int currentAppointmentID) throws SQLException {
        ObservableList<Appointment> allAppointments = getAllAppointments();
        boolean collision = false;

        for (int i = 0; i < allAppointments.size(); i++) {
            LocalDateTime appointmentStart = allAppointments.get(i).getStartTime();
            LocalDateTime appointmentEnd = allAppointments.get(i).getEndTime();

            // Exclude collisions with the current appointment
            if (allAppointments.get(i).getAppointmentID() != currentAppointmentID) {
                // Check for collisions with new prospective start time
                if (end.isEqual(appointmentEnd) || (end.isAfter(appointmentStart) && end.isBefore(appointmentEnd))) {
                    collision = true;
                    break;
                }
            }
        }
        return collision;
    }

    /**
     * Determines if there are any collisions between the provided times and any appointments in the database
     * @param start a start time to compare all existing appointment times to
     * @param end   an end time to compare all existing appointment times to
     * @return true if the given time interferes with an existing appointment. false if there is no interference.
     * @throws SQLException if a SQL exception occurs
     */
    public static boolean collisionWithInteriorAppointment(LocalDateTime start, LocalDateTime end) throws SQLException {
        return collisionWithInteriorAppointment(start,end,-1);
    }

    /**
     * Determines if there are any collisions between the provided times and any appointments in the database,
     * excluding the provided appointment id
     * @param start                a start time to compare all existing appointment times to
     * @param end                  an end time to compare all existing appointment times to
     * @param currentAppointmentID the id number of the appointment to exclude from the collision calculation
     * @return true if the given time interferes with an existing appointment. false if there is no interference.
     * @throws SQLException if a SQL exception occurs
     */
    public static boolean collisionWithInteriorAppointment(LocalDateTime start, LocalDateTime end, int currentAppointmentID) throws SQLException {
        ObservableList<Appointment> allAppointments = getAllAppointments();
        boolean collision = false;

        for (int i = 0; i < allAppointments.size(); i++) {
            LocalDateTime appointmentStart = allAppointments.get(i).getStartTime();
            LocalDateTime appointmentEnd = allAppointments.get(i).getEndTime();

            // Exclude collisions with the current appointment
            if (allAppointments.get(i).getAppointmentID() != currentAppointmentID) {
                // Check for collisions with new prospective start & end times
                if (start.isEqual(appointmentStart) || end.isEqual(appointmentEnd) || (start.isBefore(appointmentStart) && end.isAfter(appointmentEnd))) {
                    collision = true;
                    break;
                }
            }
        }
        return collision;
    }

    /**
     * Inserts the provided appointment into the database. Displays an error message if the insertion is unsuccessful.
     * @param newAppointment appointment to add into the database
     * @return number of rows that were affected during the insertion attempt
     */
    public static int addAppointment(Appointment newAppointment) {
        int rowsAffected = 0;
        title = newAppointment.getTitle();
        description = newAppointment.getDescription();
        location = newAppointment.getLocation();
        type = newAppointment.getType();
        startTime = newAppointment.getStartTime();
        endTime = newAppointment.getEndTime();
        createdBy = LoginWindowController.getLoggedInUser().getUserName();
        customerID = newAppointment.getCustomerID();
        contactID = newAppointment.getContactID();
        userID = newAppointment.getUserID();

        // Time conversion to UTC
        LocalDateTime startTimeUTC = TimeConversions.convertLocalToUTC(startTime);
        LocalDateTime endTimeUTC = TimeConversions.convertLocalToUTC(endTime);
        LocalDateTime currentUTC = TimeConversions.convertLocalToUTC(LocalDateTime.now());

        String inq = "INSERT INTO appointments (Title, Description, " +
        "Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, " +
                "User_ID, Contact_ID) VALUES ('" + title + "', '" + description + "', '" + location  + "', '" +
                type + "', '" + startTimeUTC + "', '" + endTimeUTC  + "', '" + currentUTC + "', '" + createdBy + "', '" +
                currentUTC + "', '" + createdBy + "', " + customerID + ", " + userID + ", " + contactID + ")";
        Operation.performOperation(inq);

        rowsAffected = Operation.getAffectedRows();

        return rowsAffected;
    }

    /**
     * Overwrites the appointment with given id with the provided appointment in the database.
     * @param appointmentID appointment id number to overwrite in the database
     * @param updatedAppointment new appointment data to overwrite the existing appointment with
     * @return number of rows that were affected during the update attempt
     */
    public static int updateAppointment(int appointmentID, Appointment updatedAppointment) {
        int rowsUpdated = 0;
        title = updatedAppointment.getTitle();
        description = updatedAppointment.getDescription();
        location = updatedAppointment.getLocation();
        type = updatedAppointment.getType();
        startTime = updatedAppointment.getStartTime();
        endTime = updatedAppointment.getEndTime();
        lastUpdatedBy = LoginWindowController.getLoggedInUser().getUserName();
        customerID = updatedAppointment.getCustomerID();
        contactID = updatedAppointment.getContactID();
        userID = updatedAppointment.getUserID();

        // Time conversion to UTC
        LocalDateTime startTimeUTC = TimeConversions.convertLocalToUTC(startTime);
        LocalDateTime endTimeUTC = TimeConversions.convertLocalToUTC(endTime);
        LocalDateTime currentUTC = TimeConversions.convertLocalToUTC(LocalDateTime.now());

        String inq = ("UPDATE appointments " +
                " SET Title = '" + title + "', Description = '" + description + "', Location = '" + location +
                "', Type = '" + type + "', Start = '" + startTimeUTC + "', End = '" + endTimeUTC + "', Last_Update = '" +
                currentUTC + "', Last_Updated_By = '" + lastUpdatedBy + "', User_ID = " + userID + ", Customer_ID = " +
                customerID + ", Contact_ID = " + contactID +
                " WHERE Appointment_ID = " + appointmentID);

        Operation.performOperation(inq);
        rowsUpdated = Operation.getAffectedRows();

        return rowsUpdated;
    }

    /**
     * Deletes the appointment with given id from the database.
     * @param appointmentID appointment id number to delete from the database
     * @return number of rows that were affected during the deletion attempt
     */
    public static int deleteAppointment(int appointmentID) {
        int rowsDeleted = 0;

        String inq = "DELETE FROM appointments WHERE Appointment_ID = " + appointmentID;
            Operation.performOperation(inq);
            rowsDeleted = Operation.getAffectedRows();

        return rowsDeleted;
    }

    /**
     * Extracts all appointment data from the given ResultSet and inputs it into a new Appointment list
     * @param set the ResultSet to extract appointment data from
     * @return a list of appointments that were extracted
     * @throws SQLException if a SQL exception occurs
     */
    private static ObservableList<Appointment> extractAppointment(ResultSet set) throws SQLException {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        while (set.next()) {
            appointmentID = set.getInt("Appointment_ID");
            title = set.getString("Title");
            description = set.getString("Description");
            location = set.getString("Location");
            type = set.getString("Type");
            startTime = set.getTimestamp("Start").toLocalDateTime();
            endTime = set.getTimestamp("End").toLocalDateTime();
            customerID = set.getInt("Customer_ID");
            userID = set.getInt("User_ID");
            contactID = set.getInt("Contact_ID");

            LocalDateTime startTimeLocal = TimeConversions.convertUTCToLocal(startTime);
            LocalDateTime endTimeLocal = TimeConversions.convertUTCToLocal(endTime);

            appointmentList.add(new Appointment(appointmentID, title,description,location, contactID, type, startTimeLocal, endTimeLocal, customerID, userID));
        }
        return appointmentList;
    }
}