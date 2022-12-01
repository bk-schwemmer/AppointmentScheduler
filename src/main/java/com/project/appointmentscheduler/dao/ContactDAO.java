package com.project.appointmentscheduler.dao;

import com.project.appointmentscheduler.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Facilitates interactions between the Controllers and Contacts in the database
 */
public class ContactDAO {
    private static int id = 0;

    /**
     * Retrieves contact with given id number from the database
     * @param contactId the id number of the contact to retrieve
     * @return contact that is retrieved. If no matching contact, this will be null.
     */
    public static Contact getContact(int contactId){
        id = contactId;
        Contact contact = null;

        try {
            Operation.performOperation("SELECT * from contacts WHERE Contact_ID = '" + id + "'");
            ResultSet contactSet = Operation.getResult();
            ObservableList<Contact> list = extractContact(contactSet);
            if (list.isEmpty()) {
                return null;
            } else {
                contact = list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contact;
    }

    /**
     * Retrieves all contacts from the database
     * @return a list of all contacts from the database
     * @throws SQLException if a SQL exception occurs
     */
    public static ObservableList<Contact> getAllContacts() throws SQLException {
        Operation.performOperation("SELECT * FROM contacts");
        ResultSet contactSet = Operation.getResult();
        return extractContact(contactSet);
    }

    /**
     * Extracts all contact data from the given ResultSet and inputs it into a new Contacts list
     * @param set the ResultSet to extract contact data from
     * @return a list of contacts that were extracted
     * @throws SQLException if a SQL exception occurs
     */
    private static ObservableList<Contact> extractContact(ResultSet set) throws SQLException {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();
        while (set.next()) {
            id = set.getInt("Contact_ID");
            String name = set.getString("Contact_Name");
            String email = set.getString("Email");

            contactList.add(new Contact(id, name, email));
        }
        return contactList;
    }
}