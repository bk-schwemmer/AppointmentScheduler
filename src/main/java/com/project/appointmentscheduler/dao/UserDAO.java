package com.project.appointmentscheduler.dao;

import com.project.appointmentscheduler.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Facilitates interactions between the Controllers and Users in the database
 */
public class UserDAO {
    private static String name = null;

    /**
     * Retrieves user with the give username from the database
     * @param userName a string with the username to search for in the database
     * @return user object corresponding to the username provided
     * @throws SQLException if a SQL exception occurs
     */
    public static User getUser(String userName) throws SQLException {
        name = userName;
        User user = null;

        try {
            Operation.performOperation("SELECT * from Users WHERE User_Name = '" + userName + "'");
            ResultSet userSet = Operation.getResult();

            ObservableList<User> list = extractUser(userSet);
            if (list.isEmpty()) {
                return null;
            } else {
                user = list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Retrieves all users from the database
     * @return list of user objects from the database
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<User> getAllUsers() throws SQLException {
        Operation.performOperation("SELECT * FROM users");
        ResultSet userSet = Operation.getResult();

        return extractUser(userSet);
    }

    /**
     * Extracts all user data from the given ResultSet and inputs it into a new user list
     * @param set the ResultSet to extract user data from
     * @return a list of users that were extracted
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<User> extractUser(ResultSet set) throws SQLException {
        ObservableList<User> userList = FXCollections.observableArrayList();
        while (set.next()) {
            int id = set.getInt("User_ID");
            name = set.getString("User_Name");
            String password = set.getString("Password");

            userList.add(new User(id, name, password));
        }
        return userList;
    }
}
