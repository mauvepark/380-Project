package edu.ucalgary.oop;

import java.time.*;
import java.util.*;

public class VictimController {
    private final VictimService service;
    private final Scanner scanner;

    // constructor
    public VictimController() {
        this.service = new VictimService();
        this.scanner = new Scanner(System.in);
    }

    // victim menu
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println ("----------- Victim Management Menu -----------");
            System.out.println("1. View active victims");
            System.out.println("2. Add a victim");
            System.out.println("3. Update victim name");
            System.out.println("4. Update victim age");
            System.out.println("5. Soft delete victim");
            System.out.println("6. Hard delete victim");
            System.out.println("7. Back");

            int choice = readInt("Please choose an option (1-7): ");

            try {
                switch (choice) {
                    case 1:
                        viewActiveVictims();
                        break;
                    case 2:
                        addVictim();
                        break;
                    case 3:
                        updateVictimName();
                        break;
                    case 4:
                        updateVictimAgeInfo();
                        break;
                    case 5:
                        softDeleteVictim();
                        break;
                    case 6:
                        hardDeleteVictim();
                        break;
                    case 7:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-7.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    // view victims
    public void viewActiveVictims() {
        List<DisasterVictim> victims = service.getActiveVictims();

        System.out.println();
        System.out.println("----------- Active Victims -----------");

        if (victims.isEmpty()) {
            System.out.println("No active victims found.");
            return;
        }

        for (DisasterVictim victim : victims) {
            printVictimSummary(victim);
        }
    }

    // add victim
    public void addVictim() {
        System.out.println();
        System.out.println("----------- Add Victim -----------");

        String firstName = readRequiredString("First name: ");
        String lastName = readOptionalString("Last name (press enter to leave blank): ");
        String comments = readOptionalString("Comments (press enter to leave blank): ");
        String gender = readOptionalString("Gender (Man, Woman, Boy, Girl, please specify, or blank): ");

        Integer locationId = readOptionalInt("Location (press enter to leave blank): ");

        System.out.println();
        System.out.println("Please choose age input type:");
        System.out.println("1. Exact birthdate");
        System.out.println("2. Approximate age");

        int ageChoice = readInt("Please choose an option (1-2): ");

        LocalDate dateOfBirth = null;
        Integer approximateAge = null;

        if (ageChoice == 1) {
            dateOfBirth = readDate("Birthdate (format: YYYY-MM-DD): ");
        } else if (ageChoice == 2) {
            approximateAge = readInt("Approximate age (in years): ");
        } else {
            System.out.println("Invalid option. Please choose 1 or 2.");
            return;
        }

        Person person = new Person(0, firstName, lastName, comments);

        DisasterVictim victim = service.addVictim(
            person,
            LocalDate.now(),
            dateOfBirth,
            approximateAge,
            gender,
            locationId
        );

        System.out.println("Victim added successfully.");
        printVictimSummary(victim);
    }

    // update victim name
    public void updateVictimName() {
        System.out.println();
        System.out.println("----------- Update Victim Name -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        String newFirstName = readRequiredString("New first name: ");
        String newLastName = readOptionalString("New last name (press enter to leave blank): ");

        service.updateVictimName(victim, newFirstName, newLastName);

        System.out.println("Victim name updated successfully.");
    }

    // update victim age info
    public void updateVictimAgeInfo() {
        System.out.println();
        System.out.println("----------- Update Victim Age -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        if (victim.getDateOfBirth() != null) {
            System.out.println("This victim already has a birthdate on file.");
            LocalDate newBirthdate = readDate("Enter updated birthdate (format: YYYY-MM-DD): ");
            service.updateVictimAgeInfo(victim, newBirthdate, null);
            System.out.println("Birthdate updated successfully.");
            return;
        }

        System.out.println("This victim currently uses approximate age.");
        System.out.println("1. Update approximate age");
        System.out.println("2. Replace approximate age with birthdate");

        int choice = readInt("Choose an option (1-2): ");

        if (choice == 1) {
            int newApproximateAge = readInt("New approximate age (in years): ");
            service.updateVictimAgeInfo(victim, null, newApproximateAge);
            System.out.println("Approximate age updated successfully.");
        } else if (choice == 2) {
            LocalDate newBirthdate = readDate("Birthdate (format: YYYY-MM-DD): ");
            service.updateVictimAgeInfo(victim, newBirthdate, null);
            System.out.println("Approximate age replaced with birthdate successfully.");
        } else {
            System.out.println("Invalid option.");
        }
    }

    // soft delete victim
    public void softDeleteVictim() {
        System.out.println();
        System.out.println("----------- Soft Delete Victim -----------");
        System.out.println("WARNING: Soft deleted victims stay in the database but will no longer appear in the application.");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        if (!confirm("Are you sure you want to soft delete this victim? (Y/N): ")) {
            System.out.println("Soft delete cancelled.");
            return;
        }

        service.softDeleteVictim(victim);
        System.out.println("Victim soft deleted successfully.");
    }

    // hard delete victim
    public void hardDeleteVictim() {
        System.out.println();
        System.out.println("----------- Hard Delete Victim -----------");
        System.out.println("WARNING: Hard delete permanently removes the victim and related records from the database.");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        if (!confirm("Are you sure you want to hard delete this victim? (Y/N): ")) {
            System.out.println("Hard delete cancelled.");
            return;
        }

        service.hardDeleteVictim(victim);
        System.out.println("Victim hard deleted successfully.");
    }

    // select victim by id from active victims
    private DisasterVictim selectVictim() {
        List<DisasterVictim> victims = service.getActiveVictims();

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

    // print victim info
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

    // validate inputs
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

    private Integer readOptionalInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            try {
                return Integer.parseInt(input);
            } catch (RuntimeException e) {
                System.out.println("Please enter a valid number or leave blank.");
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