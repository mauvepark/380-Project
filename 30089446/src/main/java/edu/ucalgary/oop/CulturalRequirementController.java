package edu.ucalgary.oop;

import java.util.*;

/**
 * Controller class to manage the cultural and religious requirements of disaster victims.
 * This class provides a menu-driven interface for users to view, add, update, and remove cultural requirements for victims.
 * It interacts with the CulturalRequirementService to perform the necessary operations and handles user input and output.
 */
public class CulturalRequirementController {
    private final CulturalRequirementService service;
    private final VictimService victimService;
    private final Scanner scanner;

    // constructor
    public CulturalRequirementController(Scanner scanner) {
        this.service = new CulturalRequirementService();
        this.victimService = new VictimService();
        this.scanner = scanner;
    }

    /**
     * Displays the menu for managing cultural and religious requirements and handles user input to perform the selected operations.
     * 
     * @throws IllegalArgumentException if the user input is invalid (e.g., selecting a non-existent victim or category).
     * @throws IllegalStateException if the operation cannot be performed due to the current state of the data (e.g., trying to add a requirement that already exists for a victim).
     * @throws RuntimeException if there is an error performing the operation (e.g., database errors).
     */
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("----------- Cultural or Religious Requirements Menu -----------");
            System.out.println("1. View available requirement categories and options");
            System.out.println("2. View requirements for a victim");
            System.out.println("3. Add requirement to victim");
            System.out.println("4. Set or update requirement for victim");
            System.out.println("5. Remove requirement from victim");
            System.out.println("6. Back");

            int choice = readInt("Please choose an option (1-6): ");

            try {
                switch (choice) {
                    case 1:
                        viewAvailableOptions();
                        break;
                    case 2:
                        viewRequirementsForVictim();
                        break;
                    case 3:
                        addRequirementToVictim();
                        break;
                    case 4:
                        setRequirementForVictim();
                        break;
                    case 5:
                        removeRequirementFromVictim();
                        break;
                    case 6:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-6.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    /**
     * Displays the available requirement categories and options. 
     * The categories and options are retrieved from the CulturalRequirementService and displayed in a user-friendly format.
     */
    public void viewAvailableOptions() {
        System.out.println();
        System.out.println("----------- Available Requirement Options -----------");

        List<String> categories = service.getSortedCategories();

        if (categories.isEmpty()) {
            System.out.println("No requirement categories available.");
            return;
        }

        for (String category : categories) {
            System.out.println(category + ":");

            List<String> options = service.getSortedOptionsForCategory(category);
            for (String option : options) {
                System.out.println(" - " + option);
            }

            System.out.println();
        }
    }

    /**
     * Displays the cultural and religious requirements for a specific victim. 
     * The user is prompted to select a victim from the list of active victims, 
     * and then the requirements for that victim are retrieved and displayed.
     */
    public void viewRequirementsForVictim() {
        System.out.println();
        System.out.println("----------- View Victim Requirements -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        List<CulturalRequirement> requirements = service.getRequirementsForVictim(victim.getPersonId());

        if (requirements.isEmpty()) {
            System.out.println("This victim has no cultural requirements.");
            return;
        }

        for (CulturalRequirement requirement : requirements) {
            printRequirement(requirement);
        }
    }

    /**
     * Adds a cultural requirement to a victim. The user is prompted to select a victim, 
     * choose a category and option for the requirement,
     */
    public void addRequirementToVictim() {
        System.out.println();
        System.out.println("----------- Add Requirement To Victim -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        String category = chooseCategory();
        String option = chooseOption(category);

        service.addRequirement(victim.getPersonId(), category, option);

        System.out.println("Requirement added successfully.");
    }

    /**
     * Sets or updates a cultural requirement for a victim. The user is prompted to select a victim,
     * choose a category and option for the requirement, and then the requirement is set for the victim. 
     * If the victim already has a requirement for the selected category, it will be updated to the
     */
    public void setRequirementForVictim() {
        System.out.println();
        System.out.println("----------- Set Or Update Requirement -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        String category = chooseCategory();
        String option = chooseOption(category);

        service.setRequirement(victim.getPersonId(), category, option);

        System.out.println("Requirement set successfully.");
    }

    /**
     * Removes a cultural requirement from a victim. The user is prompted to select a victim,
     * then select which requirement to remove from that victim's list of requirements.
     * The selected requirement is then removed from the victim.
     */
    public void removeRequirementFromVictim() {
        System.out.println();
        System.out.println("----------- Remove Requirement From Victim -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        List<CulturalRequirement> requirements = service.getRequirementsForVictim(victim.getPersonId());

        if (requirements.isEmpty()) {
            System.out.println("This victim has no cultural or religious requirements.");
            return;
        }

        for (int i = 0; i < requirements.size(); i++) {
            CulturalRequirement requirement = requirements.get(i);
            System.out.println((i + 1) + ". " + requirement.getCategory() + " -> " + requirement.getOption());
        }

        int choice = readInt("Please choose a requirement to remove: ");

        if (choice < 1 || choice > requirements.size()) {
            System.out.println("Invalid option.");
            return;
        }

        CulturalRequirement selected = requirements.get(choice - 1);

        if (!confirm("Are you sure you want to remove this requirement? (Y/N): ")) {
            System.out.println("Requirement removal cancelled.");
            return;
        }

        service.removeRequirement(victim.getPersonId(), selected.getCategory());
        System.out.println("Requirement removed successfully.");
    }

    /**
     * Prompts the user to select a victim from the list of active victims. The victim
     * is selected by entering the victim's ID. If the entered ID does not correspond to an active victim,
     * an error message is displayed and the user is prompted to try again.
     * 
     * @return The selected DisasterVictim object, or null if no valid victim was selected.
     */
    private DisasterVictim selectVictim() {
        List<DisasterVictim> victims = victimService.getActiveVictims();

        if (victims.isEmpty()) {
            System.out.println("No active victims found.");
            return null;
        }

        for (DisasterVictim victim : victims) {
            printVictimSummary(victim);
        }

        int victimId = readInt("Please enter victim ID: ");

        for (DisasterVictim victim : victims) {
            if (victim.getPersonId() == victimId) {
                return victim;
            }
        }

        System.out.println("No active victim found with that ID. Please try again.");
        return null;
    }

    /**
     * Prompts the user to choose a requirement category from the list of available categories. 
     * The categories are retrieved from the CulturalRequirementService and displayed in a numbered list. 
     * The user selects a category by entering the corresponding number. 
     * If the entered number is invalid, an error message is displayed and the user is prompted to try again.
     * 
     * @return The name of the selected category. or an error message is displayed if no valid selection was made.
     */
    private String chooseCategory() {
        List<String> categories = service.getSortedCategories();

        while (true) {
            System.out.println();
            System.out.println("Choose requirement category:");

            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i));
            }

            int choice = readInt("Please choose an option (1-" + categories.size() + "): ");

            if (choice >= 1 && choice <= categories.size()) {
                return categories.get(choice - 1);
            }

            System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * Prompts the user to choose a requirement option for a given category. 
     * The options for the category are retrieved from the CulturalRequirementService and displayed in a numbered list.
     * 
     * @param category The category for which to choose an option. The options displayed will be specific to this category.
     * @return The name of the selected option. An error message is displayed if no valid selection was made.
     */
    private String chooseOption(String category) {
        List<String> options = service.getSortedOptionsForCategory(category);

        while (true) {
            System.out.println();
            System.out.println("Choose option for " + category + ":");

            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ". " + options.get(i));
            }

            int choice = readInt("Please choose an option (1-" + options.size() + "): ");

            if (choice >= 1 && choice <= options.size()) {
                return options.get(choice - 1);
            }

            System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * Prints a summary of a cultural requirement in a user-friendly format. This includes the category and option of the requirement.
      * 
      * @param requirement The CulturalRequirement object to be printed.
     */
    private void printRequirement(CulturalRequirement requirement) {
        System.out.println("Category: " + requirement.getCategory() + " | Option: " + requirement.getOption());
    }

    private void printVictimSummary(DisasterVictim victim) {
        String ageInfo;
        if (victim.getDateOfBirth() != null) {
            ageInfo = "Birthdate: " + victim.getDateOfBirth();
        } else {
            ageInfo = "Approximate age: " + victim.getApproximateAge();
        }

        String lastName = victim.getLastName();
        if (lastName == null) {
            lastName = "";
        }

        System.out.println("ID: " + victim.getPersonId() + " | Name: " + victim.getFirstName() + " " + lastName
                            + " | " + ageInfo + " | Gender: " + victim.getGender() + " | Entry Date: " + victim.getEntryDate());
    }

    /**
     * Reads an integer input from the user with the given prompt. 
     * If the user enters an invalid integer, an error message is displayed and the user is prompted to try again
     *  until a valid integer is entered.
     * 
     * @param prompt The message to display to the user when asking for input.
     * @return The integer value entered by the user. An error message is displayed if the input is not a valid integer, and the user is prompted to try again.
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (RuntimeException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Prompts the user for a yes/no confirmation with the given prompt message. 
     * The user must enter "Y", "Yes", "N", or "No" (case-insensitive).
     * 
     * @param prompt The message to display to the user when asking for confirmation.
     * @return true if the user confirms with "Y" or "Yes", false if the user declines with "N" or "No". An error message is displayed if the input is invalid, and the user is prompted to try again.
     */
    private boolean confirm(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }

            System.out.println("Please enter Y or N.");
        }
    }
}