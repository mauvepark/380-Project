package edu.ucalgary.oop;

import java.io.Serializable;
import java.util.*;

public class CulturalOptions implements Serializable {
    private static final long serialVersionUID = 1L;
    private final HashMap<String, Set<String>> accommodations;

    // constructor
    public CulturalOptions(HashMap<String, Set<String>> accommodations) {
        this.accommodations = accommodations;
        }
        
    // getter
    public Map<String, Set<String>> getAccomodations() {
        return accommodations;
    }
}