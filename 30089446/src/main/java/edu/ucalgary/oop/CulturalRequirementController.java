package edu.ucalgary.oop;

import java.util.*;

public class CulturalRequirementController {
    private final CulturalRequirementService service;
    private final VictimService victimService;
    private final Scanner scanner;

    // constructor
    public CulturalRequirementController() {
        CulturalRequirementRepository repository = new CulturalRequirementRepository(DatabaseManager.getInstance().getConnection());
        CulturalRequirementLoader loader = new CulturalRequirementLoader();

        this.service = new CulturalRequirementService(repository, loader);
        this.victimService = new VictimService();
        this.scanner = new Scanner(System.in);
    }

    // menu
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

    // view all categories/options from file
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

    // view requirements for one victim
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

    // add requirement
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

    // set or update requirement
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

    // remove requirement
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

    // select victim
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

        String lastName = victim.getPerson().getLastName();
        if (lastName == null) {
            lastName = "";
        }

        System.out.println("ID: " + victim.getPersonId() + " | Name: " + victim.getPerson().getFirstName() + " " + lastName
                            + " | " + ageInfo + " | Gender: " + victim.getGender() + " | Entry Date: " + victim.getEntryDate());
    }

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