package com.project.appointmentscheduler.dao;

import com.project.appointmentscheduler.model.Division;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Facilitates interactions between the Controllers and Divisions in the database
 */
public class DivisionDAO {
    private static int divisionID;
    private static int countryID;

    /**
     * Retrieves the division corresponding to the given id number.
     * @param id the id number of the division to fetch
     * @return the division corresponding to the given id number
     */
    public static Division getDivision(int id) {
        divisionID = id;
        Division division = null;
        try {
            Operation.performOperation("SELECT * FROM first_level_divisions WHERE Division_ID = '" + divisionID + "'");
            ResultSet divisionSet = Operation.getResult();
            ObservableList<Division> list = extractDivisions(divisionSet);
            if (list.isEmpty()) {
                return null;
            } else {
                division = list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return division;
    }

    /**
     * Retrieves the divisions corresponding to the given country id number.
     * @param id the id number of the country to fetch the divisions of
     * @return a list of all divisions corresponding to the given country id number
     * @throws SQLException if SQL exception occurs
     */
    public static ObservableList<Division> getDivisionByCountryID(int id) throws SQLException {
        countryID = id;
        Operation.performOperation("SELECT * FROM first_level_divisions WHERE Country_ID = " + countryID);
        ResultSet divisionSet = Operation.getResult();

        return extractDivisions(divisionSet);
    }

    /**
     * Extracts all division data from the given ResultSet and inputs it into a new division list
     * @param set the ResultSet to extract division data from
     * @return a list of divisions that were extracted
     * @throws SQLException if a SQL exception occurs
     */
    private static ObservableList<Division> extractDivisions(ResultSet set) throws SQLException {
        ObservableList<Division> divisionList = FXCollections.observableArrayList();
        while (set.next()) {
            divisionID = set.getInt("Division_ID");
            String divisionName = set.getString("Division");
            countryID =  set.getInt("Country_ID");

            divisionList.add(new Division(divisionID, divisionName, countryID));
        }

        return divisionList;
    }
}
