package edu.ucalgary.oop;

import java.io.Serializable;
import java.util.*;

/**
 * Class to represent the cultural options for a disaster victim.
 */
public class CulturalOptions implements Serializable {
    private static final long serialVersionUID = 1L;
    private final HashMap<String, Set<String>> accommodations;

    public CulturalOptions(HashMap<String, Set<String>> accommodations) {
        this.accommodations = accommodations;
        }
        
    /**
     * Returns an unmodifiable view of the cultural accommodations options.
     * The map contains categories as keys and sets of options as values.
    */
    public Map<String, Set<String>> getAccomodations() {
        return accommodations;
    }
}