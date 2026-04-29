package edu.ucalgary.oop;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton class to log actions performed in the system. 
 * Logs are written to a text file in the format
 * [yyyy-MM-dd] | action_type | description
 */
public class ActionLogger {
    private static ActionLogger instance;
    private static final String LOG_PATH = "data/action_log.txt";

    public static ActionLogger getInstance() {
        if (instance == null) {
            instance = new ActionLogger();
        }
        return instance;
    }

    /**
     * Logs an action to the log file with the current timestamp, action type, and description.
     * 
     * @param actionType A string representing the type of action (e.g., "ADDED", "UPDATED", "DELETED").
     * @param description A detailed description of the action performed.
     */
    public void log(String actionType, String description) {
        String line = "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+ "] " + actionType + " | " + description;

        try {
            Path logPath = Paths.get(LOG_PATH);
            Path parent = logPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (FileWriter writer = new FileWriter(LOG_PATH, true)) {
                writer.write(line);
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Could not write to action log.");
        }
    }
}