package edu.ucalgary.oop;

import java.util.*;

public class CulturalRequirementService {
    private final CulturalRequirementRepository repository;
    private final Map<String, Set<String>> options;
    private final ActionLogger logger;

    // constructor
    public CulturalRequirementService() {
        this.repository = new CulturalRequirementRepository(DatabaseManager.getInstance().getConnection());
        this.options = new CulturalRequirementLoader().getAccomodations();
        this.logger = ActionLogger.getInstance();
    }

    // getters
    public Map<String, Set<String>> getAvailableOptions() {
        return Collections.unmodifiableMap(options);
    }

    public List<CulturalRequirement> getRequirementsForVictim(int victimId) {
        validateVictimId(victimId);
        return repository.getRequirementsForVictim(victimId);
    }

    // add requirment to a victim, no duplicate
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

    // remove requirement for a victim
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

    // get categories for UI
    public List<String> getSortedCategories() {
        List<String> categories = new ArrayList<>(options.keySet());
        Collections.sort(categories);
        return categories;
    }

    // get options for each category for UI
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

    // validate victim id
    private void validateVictimId(int victimId) {
        if (victimId <= 0) {
            throw new IllegalArgumentException("Victim ID must be positive");
        }
    }

    // validate category and option
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