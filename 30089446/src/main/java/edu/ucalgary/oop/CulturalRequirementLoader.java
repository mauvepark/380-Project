package edu.ucalgary.oop;

import java.io.*;
import java.util.*;

/**
 * Class to load the cultural requirements options from a serialized file. 
 * This class reads the available cultural accommodations from a .ser file.
 */
public class CulturalRequirementLoader {
    private final Map<String, Set<String>> options;
    private final String FILE = "available_requirements.ser";

    public CulturalRequirementLoader() {
        this.options = load();
    }

    /**
     * Loads the cultural accommodations options from the specified .ser file.
     * 
     * @return A map containing the cultural requirement categories as keys and sets of options as values. An exception is thrown if the file cannot be loaded or if it does not contain valid data.
     */
    private Map<String, Set<String>> load() {
        try (InputStream input =
                    getClass().getClassLoader().getResourceAsStream(FILE)) {

            if (input == null) {
                throw new RuntimeException("Missing file");
            }

            try (ObjectInputStream stream = new ObjectInputStream(input)) {
                CulturalOptions data = (CulturalOptions) stream.readObject();

                if (data == null || data.getAccomodations() == null) {
                    throw new RuntimeException("file contains no options");
                }

                return data.getAccomodations();
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not load file", e);
        }
    }

    public Map<String, Set<String>> getAccomodations() {
        return Collections.unmodifiableMap(options);
    }
}