package edu.ucalgary.oop;

import java.util.*;

/**
 * Service class to manage the cultural and religious requirements of disaster victims. 
 * This class provides methods to add, update, and remove cultural requirements for victims, 
 * as well as retrieve the available options and existing requirements for a victim. 
 */
public class CulturalRequirementService {
    private final CulturalRequirementRepository repository;
    private final Map<String, Set<String>> options;
    private final ActionLogger logger;

    public CulturalRequirementService() {
        this(
            new CulturalRequirementRepository(DatabaseManager.getInstance().getConnection()),
            new CulturalRequirementLoader().getAccomodations(),
            ActionLogger.getInstance()
        );
    }

    public CulturalRequirementService(CulturalRequirementRepository repository, Map<String, Set<String>> options, ActionLogger logger) {
        this.repository = repository;
        this.options = options;
        this.logger = logger;
    }

    // getters
    public Map<String, Set<String>> getAvailableOptions() {
        return Collections.unmodifiableMap(options);
    }

    public List<CulturalRequirement> getRequirementsForVictim(int victimId) {
        validateVictimId(victimId);
        return repository.getRequirementsForVictim(victimId);
    }

    /**
     * Adds a new cultural requirement for a victim. 
     * This method checks if the victim already has a requirement for the specified category and throws an exception if so.
     * 
     * @param victimId The ID of the victim for whom the requirement is being added.
     * @param category The category of the requirement (e.g., "Dietary", "Religious").
     * @param option The specific option for the requirement (e.g., "Halal", "Kosher").
     * @throws IllegalArgumentException if the victim already has a requirement for the specified category or if the input is invalid.
     * @throws RuntimeException if there is an error inserting the requirement into the database.
     */
    public void addRequirement(int victimId, String category, String option) {
        validateVictimId(victimId);
        validateCategoryAndOption(category, option);

        if (repository.victimHasRequirement(victimId, category)) {
            throw new IllegalArgumentException("Victim already has a requirement for category: " + category);
        }

        repository.insertRequirement(victimId, category, option);

        logger.log("ADDED", "cultural requirement for victim " + victimId + " | Category: " + category + " | Option: " + option);
    }

    // requirement setter
    public void setRequirement(int victimId, String category, String option) {
        validateVictimId(victimId);
        validateCategoryAndOption(category, option);

        List<CulturalRequirement> existingReqs = repository.getRequirementsForVictim(victimId);
        String oldOption = null;
        
        // checks if a requirement exists
        for (CulturalRequirement req : existingReqs) {
            if (req.getCategory().equals(category)) {
                oldOption = req.getOption();
                break;
            }
        }

        // if no existing requirement, insert. otherwise, update existing
        if (oldOption == null) {
            repository.insertRequirement(victimId, category, option);
            logger.log("ADDED", "cultural requirement for victim " + victimId + " | Category: " + category + " | Option: " + option);
        } else {
            repository.updateRequirement(victimId, category, option);
            logger.log("UPDATED", "cultural requirement for victim " + victimId + " | Category: " + category + " | Option: " + oldOption + " -> " + option);
        }
    }

    /**
     * Removes an existing cultural requirement for a victim. 
     * This method checks if the requirement exists before attempting to delete it.
     * 
     * @param victimId The ID of the victim for whom the requirement is being removed.
     * @param category The category of the requirement being removed (e.g., "Dietary", "Religious").
     * @throws IllegalArgumentException if the requirement does not exist for the victim or if the input is invalid.
     * @throws RuntimeException if there is an error deleting the requirement from the database.
     */
    public void removeRequirement(int victimId, String category) {
        validateVictimId(victimId);

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }

        if (!repository.victimHasRequirement(victimId, category)) {
            throw new IllegalArgumentException("No requirement found for victim " + victimId + " in category " + category);
        }

        repository.deleteRequirement(victimId, category);

        logger.log("DELETED", "cultural requirement for victim " + victimId + " | Category: " + category);
    }

    /**
     * Checks if a victim already has a cultural requirement for a specific category. 
     * This is used to prevent duplicate entries for the same category.
     * 
     * @throws IllegalArgumentException if the input is invalid.
     * @throws RuntimeException if there is an error checking the requirement in the database.
     * @return sorted list of categories for UI display.
     */
    public List<String> getSortedCategories() {
        List<String> categories = new ArrayList<>(options.keySet());
        Collections.sort(categories);
        return categories;
    }

    /**
     * Gets the sorted options for a specific category for UI display.
     * 
     * @param category The category for which to get options.
     * @return A list of sorted options for the specified category.
     * @throws IllegalArgumentException if the input is invalid.
     * @throws RuntimeException if there is an error checking the options in the database.
     */
    public List<String> getSortedOptionsForCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }

        Set<String> validOptions = this.options.get(category);
        if (validOptions == null) {
            throw new IllegalArgumentException("Unknown category: " + category);
        }

        List<String> sorted = new ArrayList<>(validOptions);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Validates the victim ID.
     * 
     * @param victimId The ID of the victim to validate.
     * @throws IllegalArgumentException if the victim ID is invalid.
     */
    private void validateVictimId(int victimId) {
        if (victimId <= 0) {
            throw new IllegalArgumentException("Victim ID must be positive");
        }
    }

    /**
     * Validates the category and option.
     * 
     * @param category The category to validate.
     * @param option The option to validate.
     * @throws IllegalArgumentException if the input is invalid.
     */
    private void validateCategoryAndOption(String category, String option) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }
        if (option == null || option.isBlank()) {
            throw new IllegalArgumentException("Option cannot be null or blank");
        }

        Set<String> validOptions = options.get(category);
        if (validOptions == null) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        // checks if option is valid for the category
        boolean match = false;
        for (String valid : validOptions) {
            if (valid.equals(option)) {
                match = true;
                break;
            }
        }

        if (!match) {
            throw new IllegalArgumentException("Invalid option '" + option + "' for category '" + category + "'");
        }
    }
}