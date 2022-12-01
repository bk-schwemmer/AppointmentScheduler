package com.project.appointmentscheduler.model;

import com.project.appointmentscheduler.dao.CountryDAO;
import com.project.appointmentscheduler.dao.DivisionDAO;

/**
 * Contains all info for a Customer object
 */
public class Customer {
    private int id;
    private String name;
    private String address;
    private String postal;
    private String phone;
    private int divisionID;
    private int countryID;

    /**
     * Constructor for object
     * @param name       a string to uniquely identify this customer
     * @param address    a string to represent this customer's address
     * @param postal     a string to represent this customer's postal code
     * @param phone      a string to represent this customer's phone number
     * @param divisionID an id number that identifies this customer's division
     * @param countryID  an id number that identifies this customer's country
     */
    public Customer(String name, String address, String postal, String phone, int divisionID, int countryID) {
        this.name = name;
        this.address = address;
        this.postal = postal;
        this.phone = phone;
        this.divisionID = divisionID;
        this.countryID = countryID;
    }

    /**
     * Constructor for object with including customer id
     * @param id         an id number to uniquely identify this customer
     * @param name       a string to uniquely identify this customer
     * @param address    a string to represent this customer's address
     * @param postal     a string to represent this customer's postal code
     * @param phone      a string to represent this customer's phone number
     * @param divisionID an id number that identifies this customer's division
     * @param countryID  an id number that identifies this customer's country
     */
    public Customer(int id, String name, String address, String postal, String phone, int divisionID, int countryID) {
        this(name, address, postal, phone, divisionID, countryID);
        this.id = id;
    }

    /**
     * Provides a string representation of this customer
     * @return a string with this customer's name and id number
     */
    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }

    /**
     * Provides the id for this customer
     * @return the id of this customer
     */
    public int getCustomerID() {
        return this.id;
    }

    /**
     * Provides the name for this customer
     * @return a string with the name of this customer
     */
    public String getName() {
        return this.name;
    }

    /**
     * Provides the address for this customer
     * @return a string with the address of this customer
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Provides the postal code for this customer
     * @return a string with the postal code of this customer
     */
    public String getPostalCode() {
        return this.postal;
    }

    /**
     * Provides the phone number for this customer
     * @return a string with the phone number of this customer
     */
    public String getPhoneNumber() {
        return this.phone;
    }

    /**
     * Provides the id for this customer's division
     * @return the id of this customer's division
     */
    public int getDivisionID() {
        return this.divisionID;
    }

    /**
     * Provides the name of this customer's division
     * @return a string of the name of this customer's division
     */
    public String getDivisionName() {
        return DivisionDAO.getDivision(divisionID).getDivisionName();
    }

    /**
     * Provides the id for this customer's country
     * @return the id of this customer's country
     */
    public int getCountryID() {
        return this.countryID;
    }

    /**
     * Provides the name of this customer's country
     * @return a string of the name of this customer's country
     */
    public String getCountryName() {
        return (CountryDAO.getCountry(countryID).getCountryName());
    }
}
