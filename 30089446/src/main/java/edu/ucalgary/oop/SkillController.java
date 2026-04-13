package edu.ucalgary.oop;

import java.time.LocalDate;
import java.util.*;

/**
 * Controller class to manage user interactions related to victim skills.
 * This class provides a menu-driven interface for viewing, adding, removing,
 * and searching victim skills.
 * It interacts with the SkillService and VictimService to perform the necessary operations on skill data.
 */
public class SkillController {
    private final SkillService service;
    private final VictimService victimService;
    private final Scanner scanner;

    /**
     * Constructor for SkillController. Initializes the SkillService, VictimService,
     * and sets up the Scanner for user input.
     * 
     * @param scanner
     */
    public SkillController(Scanner scanner) {
        this.service = new SkillService();
        this.victimService = new VictimService();
        this.scanner = scanner;
    }

    /**
     * Displays the skill registry menu and handles user input to perform various operations on victim skills,
     * such as viewing skills for a victim, adding a skill, removing a skill, and searching victims by skill category.
     * The menu continues to display until the user chooses to go back.
     * 
     * @throws RuntimeException if an unexpected error occurs while processing a menu operation
     */
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("----------- Skill Registry Menu -----------");
            System.out.println("1. View skills for a victim");
            System.out.println("2. Add skill to victim");
            System.out.println("3. Remove a skill from victim");
            System.out.println("4. Search victims by skill category");
            System.out.println("5. Back");

            int choice = readInt("Please choose an option (1-5): ");

            try {
                switch (choice) {
                    case 1:
                        viewSkillsForVictim();
                        break;
                    case 2:
                        addSkillToVictim();
                        break;
                    case 3:
                        removeSkillFromVictim();
                        break;
                    case 4:
                        searchVictimsByCategory();
                        break;
                    case 5:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-5.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    /**
     * Displays all registered skills for a selected victim.
     * The user is first prompted to choose an active victim, after which each associated skill is displayed.
     * If the victim has no registered skills, the user is informed accordingly.
     * 
     * @throws RuntimeException if the victim skills cannot be retrieved due to a service error
     */
    public void viewSkillsForVictim() {
        System.out.println();
        System.out.println("----------- View Victim Skills -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        List<VictimSkill> victimSkills = service.getSkillsForVictim(victim.getPersonId());

        if (victimSkills.isEmpty()) {
            System.out.println("This victim has no registered skills.");
            return;
        }

        for (VictimSkill victimSkill : victimSkills) {
            printVictimSkill(victimSkill);
        }
    }

    /**
     * Prompts the user to select a victim and enter the information needed to add a new skill.
     * The required input varies depending on the skill category selected by the user.
     * Once all necessary information is collected, the skill is added to the selected victim.
     * 
     * @throws RuntimeException if the skill cannot be added due to invalid input or service errors
     */
    public void addSkillToVictim() {
        System.out.println();
        System.out.println("----------- Add Skill To Victim -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        String category = chooseCategory();
        String skillName;
        String details;
        String languageCapabilities = null;
        LocalDate certificationExpiry = null;
        String proficiencyLevel = chooseProficiencyLevel();

        if (category.equals("medical")) {
            skillName = chooseMedicalSkill();
            details = readRequiredString("Certification details: ");
            certificationExpiry = readDate("Certification expiry date (format: YYYY-MM-DD): ");
        } else if (category.equals("language")) {
            skillName = readRequiredString("Language name: ");
            languageCapabilities = chooseLanguageCapabilities();
            details = readOptionalString("Details (press enter to leave blank): ");
            if (details.isEmpty()) {
                details = null;
            }
        } else {
            skillName = chooseTradeSkill();
            details = readOptionalString("Details (press enter to leave blank): ");
            if (details.isEmpty()) {
                details = null;
            }
        }

        service.addSkillToVictim(victim.getPersonId(), skillName, category, details, languageCapabilities, certificationExpiry,
                                 proficiencyLevel);

        System.out.println("Skill added successfully.");
    }

    /**
     * Allows the user to remove an existing skill from a selected victim.
     * The current skills for the victim are displayed, and the user is prompted to choose one by victim skill ID.
     * A confirmation step is required before the skill is removed.
     * 
     * @throws RuntimeException if the skill cannot be removed due to invalid input or service errors
     */
    public void removeSkillFromVictim() {
        System.out.println();
        System.out.println("----------- Remove Skill From Victim -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        List<VictimSkill> victimSkills = service.getSkillsForVictim(victim.getPersonId());

        if (victimSkills.isEmpty()) {
            System.out.println("This victim has no registered skills.");
            return;
        }

        for (VictimSkill victimSkill : victimSkills) {
            printVictimSkill(victimSkill);
        }

        int victimSkillId = readInt("Please enter victim skill ID to remove: ");

        if (!confirm("Are you sure you want to remove this skill? (Y/N): ")) {
            System.out.println("Skill removal cancelled.");
            return;
        }

        service.removeVictimSkill(victimSkillId);
        System.out.println("Skill removed successfully.");
    }

    /**
     * Searches for active victims who have skills in a selected category.
     * The matching active victims are displayed in summary format.
     * If no matching victims are found, the user is informed accordingly.
     * 
     * @throws RuntimeException if the victim search cannot be completed due to a service error
     */
    public void searchVictimsByCategory() {
        System.out.println();
        System.out.println("----------- Search Victims By Skill Category -----------");

        String category = chooseCategory();
        List<Integer> victimIds = service.searchVictimIdsBySkillCategory(category);

        if (victimIds.isEmpty()) {
            System.out.println("No active victims found with " + category + " skills.");
            return;
        }

        List<DisasterVictim> activeVictims = victimService.getActiveVictims();

        boolean found = false;
        for (DisasterVictim victim : activeVictims) {
            if (victimIds.contains(victim.getPersonId())) {
                printVictimSummary(victim);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No active victims found with " + category + " skills.");
        }
    }

    /**
     * Displays a list of active victims and prompts the user to select one by ID.
     * If the entered ID does not match an active victim, the user is informed and {@code null} is returned.
     * 
     * @return the selected DisasterVictim, or null if no matching active victim is found
     * @throws RuntimeException if the list of active victims cannot be retrieved
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
     * Displays a summary of the given victim, including identifying details such as name,
     * age or birthdate, gender, and entry date.
     * 
     * @param victim the victim whose summary should be displayed
     */
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
     * Displays a formatted summary of a victim skill, including the skill name, category,
     * proficiency level, and any optional details such as language capabilities or certification expiry.
     * 
     * @param victimSkill the victim skill record to display
     */
    private void printVictimSkill(VictimSkill victimSkill) {
        Skill matchedSkill = findSkillById(victimSkill.getSkillId());

        String skillName = "Unknown";
        String category = "Unknown";

        if (matchedSkill != null) {
            skillName = matchedSkill.getSkillName();
            category = matchedSkill.getCategory();
        }

        System.out.print("Victim Skill ID: " + victimSkill.getId() + " | Skill: " + skillName + " | Category: " + category 
                         + " | Proficiency: " + victimSkill.getProficiencyLevel());

        if (victimSkill.getDetails() != null) {
            System.out.print(" | Details: " + victimSkill.getDetails());
        }

        if (victimSkill.getLanguageCapabilities() != null) {
            System.out.print(" | Language Capabilities: " + victimSkill.getLanguageCapabilities());
        }

        if (victimSkill.getCertificationExpiry() != null) {
            System.out.print(" | Certification Expiry: " + victimSkill.getCertificationExpiry());
        }

        System.out.println();
    }

    /**
     * Searches for a skill by its ID from the list of all available skills.
     * 
     * @param skillId the ID of the skill to search for
     * @return the matching Skill if found, otherwise null
     * @throws RuntimeException if the list of available skills cannot be retrieved
     */
    private Skill findSkillById(int skillId) {
        List<Skill> skills = service.getAllSkills();

        for (Skill skill : skills) {
            if (skill.getId() == skillId) {
                return skill;
            }
        }

        return null;
    }

    /**
     * Prompts the user to choose a skill category from the available options.
     * The user is repeatedly prompted until a valid selection is made.
     * 
     * @return the selected skill category
     */
    private String chooseCategory() {
        while (true) {
            System.out.println();
            System.out.println("Choose skill category:");
            System.out.println("1. Medical");
            System.out.println("2. Language");
            System.out.println("3. Trade");

            int choice = readInt("Please choose an option (1-3): ");

            if (choice == 1) {
                return "medical";
            }
            if (choice == 2) {
                return "language";
            }
            if (choice == 3) {
                return "trade";
            }

            System.out.println("Invalid option. Please choose 1-3.");
        }
    }

    /**
     * Prompts the user to choose a medical skill from the predefined options.
     * The user is repeatedly prompted until a valid selection is made.
     * 
     * @return the selected medical skill name
     */
    private String chooseMedicalSkill() {
        while (true) {
            System.out.println();
            System.out.println("Choose medical skill:");
            System.out.println("1. first-aid");
            System.out.println("2. counseling");
            System.out.println("3. nursing");
            System.out.println("4. doctor");

            int choice = readInt("Please choose an option (1-4): ");

            if (choice == 1) {
                return "first-aid";
            }
            if (choice == 2) {
                return "counseling";
            }
            if (choice == 3) {
                return "nursing";
            }
            if (choice == 4) {
                return "doctor";
            }

            System.out.println("Invalid option. Please choose 1-4.");
        }
    }

    /**
     * Prompts the user to choose a trade skill from the predefined options.
     * The user is repeatedly prompted until a valid selection is made.
     * 
     * @return the selected trade skill name
     */
    private String chooseTradeSkill() {
        while (true) {
            System.out.println();
            System.out.println("Choose trade skill:");
            System.out.println("1. carpentry");
            System.out.println("2. plumbing");
            System.out.println("3. electricity");

            int choice = readInt("Please choose an option (1-3): ");

            if (choice == 1) {
                return "carpentry";
            }
            if (choice == 2) {
                return "plumbing";
            }
            if (choice == 3) {
                return "electricity";
            }

            System.out.println("Invalid option. Please choose 1-3.");
        }
    }

    /**
     * Prompts the user to choose language capabilities for a language skill.
     * The user is repeatedly prompted until a valid selection is made.
     * 
     * @return the selected language capability description
     */
    private String chooseLanguageCapabilities() {
        while (true) {
            System.out.println();
            System.out.println("Choose language capabilities:");
            System.out.println("1. read/write");
            System.out.println("2. speak/listen");
            System.out.println("3. read/write, speak/listen");

            int choice = readInt("Please choose an option (1-3): ");

            if (choice == 1) {
                return "read/write";
            }
            if (choice == 2) {
                return "speak/listen";
            }
            if (choice == 3) {
                return "read/write, speak/listen";
            }

            System.out.println("Invalid option. Please choose 1-3.");
        }
    }

    /**
     * Prompts the user to choose a proficiency level from the available options.
     * The user is repeatedly prompted until a valid selection is made.
     * 
     * @return the selected proficiency level
     */
    private String chooseProficiencyLevel() {
        while (true) {
            System.out.println();
            System.out.println("Choose proficiency level:");
            System.out.println("1. beginner");
            System.out.println("2. intermediate");
            System.out.println("3. advanced");

            int choice = readInt("Please choose an option (1-3): ");

            if (choice == 1) {
                return "beginner";
            }
            if (choice == 2) {
                return "intermediate";
            }
            if (choice == 3) {
                return "advanced";
            }

            System.out.println("Invalid option. Please choose 1-3.");
        }
    }

    /**
     * Utility method to read an integer from user input with a prompt.
     * It continues to prompt the user until a valid integer is entered.
     * 
     * @param prompt the message to display to the user when asking for input
     * @return the integer value entered by the user
     * @throws RuntimeException if the user input cannot be parsed as an integer
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
     * Utility method to read a date from user input with a prompt.
     * It continues to prompt the user until a valid date in YYYY-MM-DD format is entered.
     * 
     * @param prompt the message to display to the user when asking for input
     * @return the date value entered by the user
     * @throws RuntimeException if the user input cannot be parsed as a valid date
     */
    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return LocalDate.parse(input);
            } catch (RuntimeException e) {
                System.out.println("Please enter a valid date in YYYY-MM-DD format.");
            }
        }
    }

    /**
     * Utility method to read a non-blank string from user input with a prompt.
     * It continues to prompt the user until a non-empty value is entered.
     * 
     * @param prompt the message to display to the user when asking for input
     * @return the non-blank string entered by the user
     */
    private String readRequiredString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("This field cannot be blank.");
        }
    }

    /**
     * Utility method to read an optional string from user input with a prompt.
     * Blank input is allowed and returned as an empty string.
     * 
     * @param prompt the message to display to the user when asking for input
     * @return the trimmed string entered by the user
     */
    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Utility method to confirm an action with the user using a yes or no prompt.
     * It continues to prompt until the user enters a valid response.
     * 
     * @param prompt the message to display to the user when asking for confirmation
     * @return true if the user confirms the action, false otherwise
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
