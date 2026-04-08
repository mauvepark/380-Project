package edu.ucalgary.oop;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActionLogger {
    private static ActionLogger instance;
    private static final String LOG_PATH = "data/action_log.txt";

    // constructor
    public static ActionLogger getInstance() {
        if (instance == null) {
            instance = new ActionLogger();
        }
        return instance;
    }

    // log action in format specified in requirements
    public void log(String actionType, String description) {
        String line = "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+ "] " + actionType + " | " + description;

        try (FileWriter writer = new FileWriter(LOG_PATH, true)) {
            writer.write(line);
            writer.write(System.lineSeparator());
        } 
        
        catch (IOException e) {
            System.err.println("Could not write to action log.");
        }
}
}