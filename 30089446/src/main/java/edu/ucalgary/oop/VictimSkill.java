package edu.ucalgary.oop;

import java.time.LocalDate;

public class VictimSkill {
    private int id;
    private int victimId;
    private int skillId;
    private String details;
    private String languageCapabilities;
    private LocalDate certificationExpiry;
    private String proficiencyLevel;

    // constructors
    public VictimSkill(int id, int victimId, int skillId, String details, String languageCapabilities, 
                       LocalDate certificationExpiry, String proficiencyLevel) {
        setId(id);
        setVictimId(victimId);
        setSkillId(skillId);
        setDetails(details);
        setLanguageCapabilities(languageCapabilities);
        setCertificationExpiry(certificationExpiry);
        setProficiencyLevel(proficiencyLevel);
    }

    public VictimSkill(int victimId, int skillId, String details, String languageCapabilities, LocalDate certificationExpiry,
                       String proficiencyLevel) {
        setVictimId(victimId);
        setSkillId(skillId);
        setDetails(details);
        setLanguageCapabilities(languageCapabilities);
        setCertificationExpiry(certificationExpiry);
        setProficiencyLevel(proficiencyLevel);
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Victim skill id cannot be negative.");
        }
        this.id = id;
    }

    public int getVictimId() {
        return victimId;
    }

    public void setVictimId(int victimId) {
        if (victimId <= 0) {
            throw new IllegalArgumentException("Victim id must be positive.");
        }
        this.victimId = victimId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        if (skillId <= 0) {
            throw new IllegalArgumentException("Skill id must be positive.");
        }
        this.skillId = skillId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        if (details == null) {
            this.details = null;
            return;
        }

        if (details.isBlank()) {
            throw new IllegalArgumentException("Details cannot be blank.");
        }

        this.details = details.trim();
    }

    public void setLanguageCapabilities(String languageCapabilities) {
        if (languageCapabilities == null) {
            this.languageCapabilities = null;
            return;
        }

        if (languageCapabilities.isBlank()) {
            throw new IllegalArgumentException("Language capabilities cannot be blank.");
        }

        this.languageCapabilities = languageCapabilities.trim();
    }

    public String getLanguageCapabilities() {
        return languageCapabilities;
    }

    public LocalDate getCertificationExpiry() {
        return certificationExpiry;
    }

    public void setCertificationExpiry(LocalDate certificationExpiry) {
        this.certificationExpiry = certificationExpiry;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        if (proficiencyLevel == null || proficiencyLevel.isBlank()) {
            throw new IllegalArgumentException("Proficiency level cannot be null or blank.");
        }

        String normalized = proficiencyLevel.trim().toLowerCase();

        if (!normalized.equals("beginner")
                && !normalized.equals("intermediate")
                && !normalized.equals("advanced")) {
            throw new IllegalArgumentException(
                "Proficiency level must be beginner, intermediate, or advanced."
            );
        }

        this.proficiencyLevel = normalized;
    }
}