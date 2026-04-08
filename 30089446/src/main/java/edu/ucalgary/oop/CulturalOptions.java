package edu.ucalgary.oop;

import java.io.Serializable;
import java.util.*;

public class CulturalOptions implements Serializable {
    private static final long serialVersionUID = 1L;
    private final HashMap<String, Set<String>> OPTIONS;

    // constructor
    public CulturalOptions(HashMap<String, Set<String>> options) {
        this.OPTIONS = options;
    }
    
    // getter
    public Map<String, Set<String>> getOptions() {
        return OPTIONS;
    }
}