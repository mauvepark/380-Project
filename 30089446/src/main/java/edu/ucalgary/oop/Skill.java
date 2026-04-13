package edu.ucalgary.oop;

/**
 * Represents a skill that can be associated with a person in the disaster
 * victim management system.
 * Stores the skill's identifier, name, and category.
 */
public class Skill {
    private int id;
    private String skillName;
    private String category;

    /**
     * Creates a skill with a known identifier.
     *
     * @param id the unique identifier for the skill
     * @param skillName the name of the skill
     * @param category the category the skill belongs to
     */
    public Skill(int id, String skillName, String category) {
        setId(id);
        setCategory(category);
        setSkillName(skillName);
    }

    /**
     * Creates a skill without assigning an identifier.
     *
     * @param skillName the name of the skill
     * @param category the category the skill belongs to
     */
    public Skill(String skillName, String category) {
        setCategory(category);
        setSkillName(skillName);
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Skill id cannot be negative.");
        }
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    // skill name must be valid for the category
    public void setSkillName(String skillName) {
        if (skillName == null || skillName.isBlank()) {
            throw new IllegalArgumentException("Skill name cannot be null or blank.");
        }

        String normalizedName = skillName.trim();
        String currentCategory = this.category == null ? null : this.category.toLowerCase();

        if ("medical".equals(currentCategory)) {
            if (!normalizedName.equalsIgnoreCase("first-aid")
                    && !normalizedName.equalsIgnoreCase("counseling")
                    && !normalizedName.equalsIgnoreCase("nursing")
                    && !normalizedName.equalsIgnoreCase("doctor")) {
                throw new IllegalArgumentException(
                    "Medical skill must be one of: first-aid, counseling, nursing, doctor."
                );
            }
        }

        if ("trade".equals(currentCategory)) {
            if (!normalizedName.equalsIgnoreCase("carpentry")
                    && !normalizedName.equalsIgnoreCase("plumbing")
                    && !normalizedName.equalsIgnoreCase("electricity")) {
                throw new IllegalArgumentException(
                    "Trade skill must be one of: carpentry, plumbing, electricity."
                );
            }
        }

        this.skillName = normalizedName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank.");
        }

        String normalizedCategory = category.trim().toLowerCase();

        if (!normalizedCategory.equals("medical")
                && !normalizedCategory.equals("language")
                && !normalizedCategory.equals("trade")) {
            throw new IllegalArgumentException(
                "Category must be medical, language, or trade."
            );
        }

        this.category = normalizedCategory;
    }
}
