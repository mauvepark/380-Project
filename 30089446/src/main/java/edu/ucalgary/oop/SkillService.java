package edu.ucalgary.oop;

import java.time.LocalDate;
import java.util.*;

/**
 * Service class for managing skills and victim skill assignments in the disaster victim management system.
 * Provides methods to retrieve skills, search skills by category, assign skills to victims,
 * and remove skill assignments while enforcing business rules and validation.
 */
public class SkillService {
    private final SkillRepository repository;
    private final ActionLogger logger;
    private final VictimRepository victimRepository;

    /**
     * Constructor for SkillService. Initializes the repositories and logger using the shared database connection.
     */
    public SkillService() {
        this.repository = new SkillRepository(DatabaseManager.getInstance().getConnection());
        this.victimRepository = new VictimRepository(DatabaseManager.getInstance().getConnection());
        this.logger = ActionLogger.getInstance();
    }

    /**
     * Constructor for SkillService with dependency injection.
     * This constructor is useful for testing or when providing custom repository and logger implementations.
     * 
     * @param repository the repository used to manage skill-related database operations
     * @param logger the logger used to record skill-related actions
     * @param victimRepository the repository used to validate victim status
     */
    public SkillService(SkillRepository repository, ActionLogger logger, VictimRepository victimRepository) {
        this.repository = repository;
        this.logger = logger;
        this.victimRepository = victimRepository;
    }

    /**
     * Retrieves all skills from the system.
     * 
     * @return a list of all skills
     * @throws RuntimeException if there is an error retrieving the skills
     */
    public List<Skill> getAllSkills() {
        return repository.getAllSkills();
    }

    /**
     * Retrieves all skills in the specified category.
     * The category is validated before the search is performed.
     * 
     * @param category the category of skills to retrieve
     * @return a list of skills in the specified category
     * @throws IllegalArgumentException if the category is null, blank, or invalid
     * @throws RuntimeException if there is an error retrieving the skills
     */
    public List<Skill> getSkillsByCategory(String category) {
        validateCategory(category);
        return repository.getSkillsByCategory(category);
    }

    /**
     * Retrieves all skill assignments for a specific victim.
     * The victim ID is validated and the victim must exist and be active.
     * 
     * @param victimId the ID of the victim whose skills are to be retrieved
     * @return a list of skill assignments for the specified victim
     * @throws IllegalArgumentException if the victim ID is invalid or the victim does not exist or is inactive
     * @throws RuntimeException if there is an error retrieving the victim skills
     */
    public List<VictimSkill> getSkillsForVictim(int victimId) {
        validateVictimId(victimId);
        validateVictimExistsAndIsActive(victimId);
        return repository.getSkillsForVictim(victimId);
    }

    /**
     * Searches for active victim IDs that have skills in the specified category.
     * The category is validated before the search is performed.
     * 
     * @param category the skill category to search for
     * @return a list of matching active victim IDs
     * @throws IllegalArgumentException if the category is null, blank, or invalid
     * @throws RuntimeException if there is an error searching for victims by skill category
     */
    public List<Integer> searchVictimIdsBySkillCategory(String category) {
        validateCategory(category);
        return repository.searchVictimIdsBySkillCategory(category);
    }

    /**
     * Adds a skill to a victim after validating the victim, category, and skill-specific business rules.
     * If the skill does not already exist in the system, it is created before being assigned to the victim.
     * 
     * @param victimId the ID of the victim receiving the skill
     * @param skillName the name of the skill to assign
     * @param category the category of the skill
     * @param details additional details about the skill assignment
     * @param languageCapabilities the language capabilities associated with the skill, if applicable
     * @param certificationExpiry the certification expiry date, if applicable
     * @param proficiencyLevel the proficiency level of the victim for the skill
     * @return the newly created VictimSkill record
     * @throws IllegalArgumentException if the input is invalid, the victim is inactive, or the victim already has the skill
     * @throws RuntimeException if there is an error inserting the skill assignment
     */
    public VictimSkill addSkillToVictim(int victimId, String skillName, String category, String details, 
                                        String languageCapabilities, LocalDate certificationExpiry, String proficiencyLevel) {
        validateVictimId(victimId);
        validateVictimExistsAndIsActive(victimId);
        validateCategory(category);

        Skill skill = getOrCreateSkill(skillName, category);

        if (repository.victimHasSkill(victimId, skill.getId())) {
            throw new IllegalArgumentException("Victim already has this skill.");
        }

        validateVictimSkillData(skill, details, languageCapabilities, certificationExpiry, proficiencyLevel);

        int victimSkillId = repository.insertVictimSkill(victimId, skill.getId(), details, languageCapabilities, 
                                                         certificationExpiry, proficiencyLevel);

        VictimSkill victimSkill = new VictimSkill(victimSkillId, victimId, skill.getId(), details, languageCapabilities,
                                                  certificationExpiry, proficiencyLevel);

        logger.log("ADDED", "skill for victim " + victimId + " | Category: " + skill.getCategory() + " | Skill: " + skill.getSkillName());

        return victimSkill;
    }

    /**
     * Removes a skill assignment from a victim based on the victim skill record ID.
     * The deletion is logged after it is completed successfully.
     * 
     * @param victimSkillId the ID of the victim skill record to remove
     * @throws IllegalArgumentException if the victim skill ID is not positive
     * @throws RuntimeException if there is an error deleting the victim skill
     */
    public void removeVictimSkill(int victimSkillId) {
        if (victimSkillId <= 0) {
            throw new IllegalArgumentException("Victim skill id must be positive.");
        }

        repository.deleteVictimSkill(victimSkillId);

        logger.log("DELETED", "victim skill " + victimSkillId);
    }

    /**
     * Deletes all skill assignments for a specific victim.
     * This method is intended for use when a victim is being hard deleted.
     * 
     * @param victimId the ID of the victim whose skills should be deleted
     * @throws IllegalArgumentException if the victim ID is invalid or the victim does not exist or is inactive
     * @throws RuntimeException if there is an error deleting the victim's skill assignments
     */
    public void deleteAllSkillsForVictim(int victimId) {
        validateVictimId(victimId);
        validateVictimExistsAndIsActive(victimId);
        repository.deleteAllSkillsForVictim(victimId);
    }

    /**
     * Retrieves an existing skill by name and category, or creates a new one if no matching skill exists.
     * 
     * @param skillName the name of the skill to retrieve or create
     * @param category the category of the skill to retrieve or create
     * @return the existing or newly created Skill
     * @throws RuntimeException if there is an error retrieving or inserting the skill
     */
    private Skill getOrCreateSkill(String skillName, String category) {
        Skill existingSkill = repository.getSkillByNameAndCategory(skillName, category);

        if (existingSkill != null) {
            return existingSkill;
        }

        int skillId = repository.insertSkill(skillName, category);
        return new Skill(skillId, skillName, category);
    }

    /**
     * Validates that a victim ID is positive.
     * 
     * @param victimId the victim ID to validate
     * @throws IllegalArgumentException if the victim ID is not positive
     */
    private void validateVictimId(int victimId) {
        if (victimId <= 0) {
            throw new IllegalArgumentException("Victim id must be positive.");
        }
    }

    /**
     * Validates that a skill category is not null, blank, and is one of the allowed values.
     * 
     * @param category the category to validate
     * @throws IllegalArgumentException if the category is null, blank, or not one of the accepted values
     */
    private void validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank.");
        }

        String normalized = category.trim().toLowerCase();

        if (!normalized.equals("medical")
                && !normalized.equals("language")
                && !normalized.equals("trade")) {
            throw new IllegalArgumentException("Category must be medical, language, or trade.");
        }
    }

    /**
     * Validates the skill assignment data based on the skill's category and business rules.
     * Different categories require or forbid different combinations of details,
     * language capabilities, and certification expiry values.
     * 
     * @param skill the skill being assigned
     * @param details additional details about the skill assignment
     * @param languageCapabilities the language capabilities associated with the skill, if applicable
     * @param certificationExpiry the certification expiry date, if applicable
     * @param proficiencyLevel the proficiency level associated with the skill
     * @throws IllegalArgumentException if the skill data does not satisfy the business rules for its category
     */
    private void validateVictimSkillData(Skill skill, String details, String languageCapabilities,
                                         LocalDate certificationExpiry, String proficiencyLevel) {
        if (skill == null) {
            throw new IllegalArgumentException("Skill cannot be null.");
        }

        if (proficiencyLevel == null || proficiencyLevel.isBlank()) {
            throw new IllegalArgumentException("Proficiency level cannot be null or blank.");
        }

        String category = skill.getCategory();
        String skillName = skill.getSkillName();

        if (category.equals("medical")) {
            if (!skillName.equalsIgnoreCase("first-aid")
                    && !skillName.equalsIgnoreCase("counseling")
                    && !skillName.equalsIgnoreCase("nursing")
                    && !skillName.equalsIgnoreCase("doctor")) {
                throw new IllegalArgumentException("Invalid medical skill.");
            }

            if (certificationExpiry == null) {
                throw new IllegalArgumentException("Medical skills require certification expiry.");
            }

            if (details == null || details.isBlank()) {
                throw new IllegalArgumentException("Medical skills require certification details.");
            }

            if (languageCapabilities != null && !languageCapabilities.isBlank()) {
                throw new IllegalArgumentException("Medical skills cannot have language capabilities.");
            }
        }

        if (category.equals("language")) {
            if (languageCapabilities == null || languageCapabilities.isBlank()) {
                throw new IllegalArgumentException("Language skills require language capabilities.");
            }

            if (!isValidLanguageCapabilities(languageCapabilities)) {
                throw new IllegalArgumentException(
                    "Language capabilities must be: read/write, speak/listen, or both."
                );
            }

            if (certificationExpiry != null) {
                throw new IllegalArgumentException("Language skills cannot have certification expiry.");
            }

            if (details != null && details.isBlank()) {
                throw new IllegalArgumentException("Language skill details cannot be blank.");
            }
        }

        if (category.equals("trade")) {
            if (!skillName.equalsIgnoreCase("carpentry") && !skillName.equalsIgnoreCase("plumbing") && !skillName.equalsIgnoreCase("electricity")) {
                throw new IllegalArgumentException("Invalid trade skill.");
            }

            if (languageCapabilities != null && !languageCapabilities.isBlank()) {
                throw new IllegalArgumentException("Trade skills cannot have language capabilities.");
            }
        }
    }

    /**
     * Checks whether the provided language capabilities value is valid.
     * 
     * @param languageCapabilities the language capabilities value to validate
     * @return true if the language capabilities value is valid, false otherwise
     */
    private boolean isValidLanguageCapabilities(String languageCapabilities) {
        String value = languageCapabilities.trim();
        if (value.equalsIgnoreCase("read/write")) {
            return true;
        }
        if (value.equalsIgnoreCase("speak/listen")) {
            return true;
        }
        if (value.equalsIgnoreCase("read/write, speak/listen")) {
            return true;
        }
        if (value.equalsIgnoreCase("speak/listen, read/write")) {
            return true;
        }

        return false;
    }

    /**
     * Validates that a victim exists and is active.
     * 
     * @param victimId the ID of the victim to validate
     * @throws IllegalArgumentException if the victim does not exist or has been soft deleted
     */
    private void validateVictimExistsAndIsActive(int victimId) {
        if (!victimRepository.isActiveVictim(victimId)) {
            throw new IllegalArgumentException("Victim does not exist or is soft deleted.");
        }
    }
}
