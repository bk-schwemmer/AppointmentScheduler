package com.project.appointmentscheduler.model;

/**
 * Contains all info for a Division object
 */
public class Division {
    private int divisionID;
    private String divisionName;
    private int countryID;

    /**
     * Contructor for this object
     * @param divisionID   an id number to uniquely identify this division
     * @param divisionName a string to uniquely identify this division
     * @param countryID    an id number that identifies this division's country
     */
    public Division (int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    /**
     * Provides the id for this division
     * @return the id of this division
     */
    public int getDivisionID () {
        return divisionID;
    }

    /**
     * Provides the name for this division
     * @return a string with the name of this division
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * Provides the id for this division's country
     * @return the id of the country for this division
     */
    public int getCountryID() {return countryID;}

    /**
     * Provides a string representation of this division
     * @return a string with this division's name and id number
     */
    @Override
    public String toString() {
        return divisionName + " (ID: " + divisionID + ")";
    }
}
