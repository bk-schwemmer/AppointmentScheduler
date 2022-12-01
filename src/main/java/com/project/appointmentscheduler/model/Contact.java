package com.project.appointmentscheduler.model;

/**
 * Contains all info for a Contact object
 */
public class Contact {
    private int id;
    private String name;
    private String email;

    /**
     * Constructor for object
     * @param id    id number to uniquely identify this contact
     * @param name  a string to identify this contact
     * @param email a string representation of the contact's email address
     */
    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Provides a string representation of this contact
     * @return a string with this contact's name and email address
     */
    @Override
    public String toString() {
        return name + " (" + email + ")";
    }

    /**
     * Provides the id for this contact
     * @return the id of this contact
     */
    public int getId() {
        return this.id;
    }

    /**
     * Provides the name for this contact
     * @return a string with the name of this contact
     */
    public String getName() {
        return this.name;
    }

    /**
     * Provides the email for this contact
     * @return a string with the email of this contact
     */
    public String getEmail() {
        return this.email;
    }
}
