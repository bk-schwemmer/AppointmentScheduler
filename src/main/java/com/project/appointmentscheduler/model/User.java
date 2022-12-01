package com.project.appointmentscheduler.model;

/**
 * Contains all info for a User object
 */
public class User {
    private int userID;
    private String userName;
    private String password;

    /**
     * Constructor for object
     * @param userID   an id number to uniquely identify this user
     * @param userName a string to uniquely identify this user
     * @param password a string used to verify this user's identity for login
     */
    public User(int userID, String userName, String password) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Provides a string representation of this user
     * @return a string with this user's name and id number
     */
    @Override
    public String toString() {
        return userName + " (ID: " + userID + ")";
    }

    /**
     * Provides the id for this user
     * @return the id of this user
     */
    public int getID() {
        return this.userID;
    }

    /**
     * Provides the username for this user
     * @return a string with the username of this user
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Provides the password for this user
     * @return a string with the password of this user
     */
    public String getPassword() {
        return this.password;
    }

}
