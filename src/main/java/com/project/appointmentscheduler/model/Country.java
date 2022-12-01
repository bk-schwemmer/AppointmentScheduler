package com.project.appointmentscheduler.model;

/**
 * Contains all info for a Country object
 */
public class Country {
    private int countryID;
    private String countryName;

    /**
     * Constructor for object
     * @param countryID   a number to uniquely identify this country
     * @param countryName a string to identify this country
     */
    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     * Provides the id for this country
     * @return the id number of this country
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * Provides the name for this country
     * @return a string with the name of this country
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Provides a string representation of this country
     * @return a string with this country's name and country id
     */
    @Override
    public String toString() {
        return countryName + " (ID: " + countryID + ")";
    }

}
