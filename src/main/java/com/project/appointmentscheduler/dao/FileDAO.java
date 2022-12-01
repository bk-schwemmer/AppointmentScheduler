package com.project.appointmentscheduler.dao;

import com.project.appointmentscheduler.utilities.TimeConversions;
import javafx.scene.control.Alert;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Facilitates interactions between the Controllers and file I/O
 */
public class FileDAO {
    private static String filename = "login_activity.txt";
    private static FileWriter outFile = null;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss");

    /**
     * Appends the successful login attempt line to the log file
     * @param userName a string indicating the username of the last user attempting to log in
     */
    public static void successfulLogin(String userName) {
        String success = "User " + userName + " successfully logged in at " + getCurrentUTCTime() + " UTC";
        writeLineToFile(success);
    }

    /**
     * Appends the unsuccessful login attempt line to the log file
     * @param userName a string indicating the username of the last user attempting to log in
     */
    public static void unsuccessfulLogin(String userName) {
        String failure = "User " + userName + " gave invalid log-in at " + getCurrentUTCTime() + " UTC";
        writeLineToFile(failure);
    }

    /**
     * Opens, or creates, a file with title 'login_activity.txt' and makes it appendable. An error is displayed if
     * the opening is unsuccessful
     */
    private static void openFile() {
        try {
             outFile = new FileWriter(filename,true);

        } catch (IOException e) {
            Alert fileOpenError = new Alert(Alert.AlertType.ERROR, "Unable to open file");
            fileOpenError.showAndWait();
        }
    }

    /**
     * Opens the log file, appends the given string as the next line and closes the file.
     * @param content a string with the line to append
     */
    private static void writeLineToFile(String content) {
            openFile();
            PrintWriter pw = new PrintWriter(outFile);
            pw.println(content);
            pw.close();
    }

    /**
     * Converts the current time into UTC
     * @return the current time in UTC
     */
    private static String getCurrentUTCTime() {
        return TimeConversions.convertLocalToUTC(LocalDateTime.now()).format(dtf);
    }
}
