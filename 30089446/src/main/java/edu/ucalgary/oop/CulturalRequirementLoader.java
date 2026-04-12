package edu.ucalgary.oop;

import java.io.*;
import java.util.*;

public class CulturalRequirementLoader {
    private final Map<String, Set<String>> options;
    private final String FILE = "available_requirements.ser";

    public CulturalRequirementLoader() {
        this.options = load();
    }

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