package edu.ucalgary.oop;

import java.time.LocalDate;
import java.util.*;

public class SkillController {
    private final SkillService service;
    private final VictimService victimService;
    private final Scanner scanner;

    // constructor
    public SkillController() {
        this.service = new SkillService(new SkillRepository(DatabaseManager.getInstance().getConnection()));
        this.victimService = new VictimService();
        this.scanner = new Scanner(System.in);
    }
    
    // skill menu
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

    // view victim skills
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

    // add skill
    public void addSkillToVictim() {
        System.out.println();
        System.out.println("----------- Add Skill To Victim -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        String category = chooseCategory();
        String skillName;
        String details = null;
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

    // remove skill
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

    // search by category
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

    // print victim summary
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

    // print victim skill
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

    private Skill findSkillById(int skillId) {
        List<Skill> skills = service.getAllSkills();

        for (Skill skill : skills) {
            if (skill.getId() == skillId) {
                return skill;
            }
        }

        return null;
    }

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

    // input helpers
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

    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
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