package com.project.appointmentscheduler.dao;

import com.project.appointmentscheduler.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Facilitates interactions between the Controllers and Countries in the database
 */
public class CountryDAO {
    private static int countryID;

    /**
     * Retrieves the country corresponding to the given id number.
     * @param id the id number of the country to fetch
     * @return the country corresponding to the given id number
     */
    public static Country getCountry(int id) {
        countryID = id;
        Country country = null;

        try {
            Operation.performOperation("SELECT * FROM countries WHERE Country_ID = '" + countryID + "'");
            ResultSet countrySet = Operation.getResult();
            ObservableList<Country> list = extractCountries(countrySet);
            if (list.isEmpty()) {
                return null;
            } else {
                country = list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return country;
    }

    /**
     * Retrieves all countries from the database
     * @return a list of all countries from the database
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<Country> getAllCountries() throws SQLException {
        Operation.performOperation("SELECT * FROM countries");
        ResultSet countrySet = Operation.getResult();
        return extractCountries(countrySet);
    }

    /**
     * Extracts all country data from the given ResultSet and inputs it into a new Countries list
     * @param set the ResultSet to extract country data from
     * @return a list of countries that were extracted
     * @throws SQLException if a SQL exception occurs
     */
    private static ObservableList<Country> extractCountries(ResultSet set) throws SQLException {
        ObservableList<Country> countryList = FXCollections.observableArrayList();
        while (set.next()) {
            countryID = set.getInt("Country_ID");
            String countryName = set.getString("Country");
            countryList.add(new Country(countryID, countryName));
        }
        return countryList;
    }
}
