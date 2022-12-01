package com.project.appointmentscheduler.model;

import com.project.appointmentscheduler.dao.ContactDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains all personal info for an Appointment object
 */
public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int customerID;
    private int userID;
    private int contactID;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");

    /**
     * Creates appointment object with given data
     * @param title       a string for the title of the appointment
     * @param description a string for the description of the appointment
     * @param location    a string for the location of the appointment
     * @param contactID   an id number for the appointment contact
     * @param type        a string for the type of appointment
     * @param startTime   the date and time the appointment starts
     * @param endTime     the date and time the appointment ends
     * @param customerID  an id number for the customer the appointment is with
     * @param userID      an id number for the user the appointment is associated with
     */
    public Appointment(String title, String description, String location, int contactID,
                    String type, LocalDateTime startTime, LocalDateTime endTime, int customerID, int userID) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactID = contactID;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerID = customerID;
        this.userID = userID;
    }

    /**
     * Creates appointment object with given data
     * @param appointmentID an id number unique to this appointment
     * @param title         a string for the title of the appointment
     * @param description   a string for the description of the appointment
     * @param location      a string for the location of the appointment
     * @param contactID     an id number for the appointment contact
     * @param type          a string for the type of appointment
     * @param startTime     the date and time the appointment starts
     * @param endTime       the date and time the appointment ends
     * @param customerID    an id number for the customer the appointment is with
     * @param userID        an id number for the user the appointment is associated with
     */
    public Appointment(int appointmentID, String title, String description, String location, int contactID,
                       String type, LocalDateTime startTime, LocalDateTime endTime, int customerID, int userID) {
        this(title, description, location, contactID, type, startTime, endTime, customerID, userID);
        this.appointmentID = appointmentID;
    }

    /**
     * Provides the appointment ID for this appointment
     * @return the id of this appointment
     */
    public int getAppointmentID() {
        return this.appointmentID;
    }

    /**
     * Provides the title for this appointment
     * @return the title of this appointment
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Provides the description for this appointment
     * @return the description of this appointment
     */
    public String getDescription() { 
        return this.description;
    }

    /**
     * Provides the location for this appointment
     * @return the location of this appointment
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Provides the type for this appointment
     * @return the type of this appointment
     */
    public String getType() {
        return this.type;
    }

    /**
     * Provides the start time for this appointment
     * @return the start time of this appointment
     */
    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    /**
     * Provides the start time for this appointment in an easy-to-read format
     * @return the formatted start time of this appointment
     */
    public String getFormattedStartTime() {
        return this.startTime.format(dtf);
    }

    /**
     * Provides the end time for this appointment
     * @return the end time of this appointment
     */
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * Provides the end time for this appointment in an easy-to-read format
     * @return the formatted end time of this appointment
     */
    public String getFormattedEndTime() {
        return this.endTime.format(dtf);
    }

    /**
     * Provides the contact id for this appointment
     * @return the id of the contact for this appointment
     */
    public int getContactID() {
        return this.contactID;
    }

    /**
     * Provides the contact name for this appointment
     * @return the name of the contact for this appointment
     */
    public String getContactName() {
        return ContactDAO.getContact(contactID).getName();
    }

    /**
     * Provides the customer id for this appointment
     * @return the id of the customer for this appointment
     */
    public int getCustomerID() {
        return this.customerID;
    }

    /**
     * Provides the user id for this appointment
     * @return the id of the user for this appointment
     */
    public int getUserID() {
        return this.userID;
    }

    /**
     * Provides a string representation of the appointment object
     * @return a string containing all appointment properties
     */
    @Override
    public String toString() {
        return ("- APPOINTMENT INFO - "
        + "\nApptID = " + appointmentID + "\nTitle = " + title + "\nDescription = " + description
        + "\nLocation = " + location + "\nContact = " + contactID
        + "\nType = " + type + "\nStart = " + startTime + "\nEnd = " + endTime
        + "\nCustomer = " + customerID + "\nUser = " + userID + "\nEND OF APPOINTMENT\n");
    }
}
