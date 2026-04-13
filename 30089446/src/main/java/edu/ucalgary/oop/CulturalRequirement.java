package edu.ucalgary.oop;

/**
 * Class to represent a specific cultural requirement for a disaster victim. 
 * Each requirement is associated with a category (e.g., "Dietary", "Religious") and 
 * a specific option within that category (e.g., "Halal", "Kosher").
 */
public class CulturalRequirement {
    private int victimId;
    private String category;
    private String option;

    // constructor
    public CulturalRequirement(int victimId, String category, String option) {
        this.victimId = victimId;
        this.category = category;
        this.option = option;
    }

    // getters and setters
    public String getCategory() {
        return category; 
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getVictimId() {
        return victimId;
    }

    public void setVictimId(int victimId) {
        this.victimId = victimId;
    }
}
