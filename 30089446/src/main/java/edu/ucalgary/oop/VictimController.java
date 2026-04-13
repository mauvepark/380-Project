package edu.ucalgary.oop;

import java.time.*;
import java.util.*;

/**
 * Controller class for managing victims.
 * This class provides menu options for viewing, adding, updating, and deleting victims.
 */
public class VictimController {
    private final VictimService service;
    private final MedicalRecordService medicalRecordService;
    private final Scanner scanner;
    private final CulturalRequirementController culturalController;

    /**
     * Constructor for VictimController.
     * Sets up the services and scanner used for victim operations.
     * 
     * @param scanner
     */
    public VictimController(Scanner scanner) {
        this.service = new VictimService();
        this.medicalRecordService = new MedicalRecordService();
        this.scanner = scanner;
        this.culturalController = new CulturalRequirementController(scanner);
    }

    /**
     * Displays the victim menu and handles user choices.
     * The menu keeps running until the user chooses to go back.
     * 
     * @throws RuntimeException if an unexpected error happens during a menu action
     */
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println("1. View active victims");
            System.out.println("2. Add a victim");
            System.out.println("3. Update victim name");
            System.out.println("4. Update victim age");
            System.out.println("5. Manage cultural requirements");
            System.out.println("6. Manage victim medical records");
            System.out.println("7. Soft delete victim");
            System.out.println("8. Hard delete victim");
            System.out.println("9. Back");

            int choice = readInt("Please choose an option (1-9): ");

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
                        culturalController.menu();
                        break;
                    case 6:
                        manageVictimMedicalRecords();
                        break;
                    case 7:
                        softDeleteVictim();
                        break;
                    case 8:
                        hardDeleteVictim();
                        break;
                    case 9:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-9.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    /**
     * Displays all active victims.
     * If there are no active victims, the user is informed.
     * 
     * @throws RuntimeException if the victims cannot be loaded
     */
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

    /**
     * Prompts the user for victim information and adds a new victim.
     * 
     * @throws RuntimeException if the victim cannot be added
     */
    public void addVictim() {
        System.out.println();
        System.out.println("----------- Add Victim -----------");

        String firstName = readRequiredString("First name: ");
        String lastName = readOptionalString("Last name (press enter to leave blank): ");
        String comments = readOptionalString("Comments (press enter to leave blank): ");
        String gender = chooseGender();
        String specifiedGender = null;

        if (gender.equals("Please specify")) {
            specifiedGender = readRequiredString("Enter custom gender: ");
        }

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
            specifiedGender,
            locationId
        );

        System.out.println("Victim added successfully.");
        printVictimSummary(victim);

        if (confirm("Would you like to add cultural or religious requirements now? (Y/N): ")) {
            culturalController.menu();
        }
    }

    /**
     * Updates the name of a selected victim.
     * 
     * @throws RuntimeException if the victim name cannot be updated
     */
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

    /**
     * Updates the age information for a selected victim.
     * The update can use either a birthdate or an approximate age.
     * 
     * @throws RuntimeException if the victim age information cannot be updated
     */
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

    /**
     * Opens the medical record submenu for a selected victim.
     * 
     * @throws RuntimeException if medical records cannot be managed
     */
    public void manageVictimMedicalRecords() {
        System.out.println();
        System.out.println("----------- Manage Victim Medical Records -----------");

        DisasterVictim victim = selectVictim();
        if (victim == null) {
            return;
        }

        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("Selected victim:");
            printVictimSummary(victim);
            System.out.println();
            System.out.println("1. View medical records");
            System.out.println("2. Add medical record");
            System.out.println("3. Update medical record");
            System.out.println("4. Delete medical record");
            System.out.println("5. Back");

            int choice = readInt("Please choose an option (1-5): ");

            switch (choice) {
                case 1:
                    viewMedicalRecordsForVictim(victim);
                    break;
                case 2:
                    addMedicalRecordForVictim(victim);
                    break;
                case 3:
                    updateMedicalRecordForVictim(victim);
                    break;
                case 4:
                    deleteMedicalRecordForVictim(victim);
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    /**
     * Displays all medical records for a victim.
     * 
     * @param victim the victim whose medical records will be shown
     * @throws RuntimeException if the medical records cannot be loaded
     */
    private void viewMedicalRecordsForVictim(DisasterVictim victim) {
        System.out.println();
        System.out.println("----------- Victim Medical Records -----------");

        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsForVictim(victim.getPersonId());

        if (records.isEmpty()) {
            System.out.println("This victim has no medical records.");
            return;
        }

        for (MedicalRecord record : records) {
            printMedicalRecordSummary(record);
        }
    }

    /**
     * Adds a medical record for a victim.
     * 
     * @param victim the victim receiving the medical record
     * @throws RuntimeException if the medical record cannot be added
     */
    private void addMedicalRecordForVictim(DisasterVictim victim) {
        System.out.println();
        System.out.println("----------- Add Medical Record -----------");

        String treatmentDetails = readRequiredString("Treatment details: ");
        LocalDate treatmentDate = readDate("Treatment date (format: YYYY-MM-DD): ");
        Integer locationId = readOptionalInt("Location ID (press enter to leave blank): ");

        MedicalRecord record = medicalRecordService.addMedicalRecord(
            victim.getPersonId(),
            treatmentDetails,
            treatmentDate,
            locationId
        );

        System.out.println("Medical record added successfully.");
        printMedicalRecordSummary(record);
    }

    /**
     * Updates a medical record for a victim.
     * 
     * @param victim the victim whose medical record will be updated
     * @throws RuntimeException if the medical record cannot be updated
     */
    private void updateMedicalRecordForVictim(DisasterVictim victim) {
        System.out.println();
        System.out.println("----------- Update Medical Record -----------");

        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsForVictim(victim.getPersonId());

        if (records.isEmpty()) {
            System.out.println("This victim has no medical records.");
            return;
        }

        for (MedicalRecord record : records) {
            printMedicalRecordSummary(record);
        }

        int recordId = readInt("Please enter medical record ID: ");

        MedicalRecord selected = null;
        for (MedicalRecord record : records) {
            if (record.getId() == recordId) {
                selected = record;
                break;
            }
        }

        if (selected == null) {
            System.out.println("No medical record found with that ID.");
            return;
        }

        String newTreatmentDetails = readRequiredString("New treatment details: ");
        LocalDate newTreatmentDate = readDate("New treatment date (format: YYYY-MM-DD): ");
        Integer newLocationId = readOptionalInt("New location ID (press enter to leave blank): ");

        medicalRecordService.updateMedicalRecord(selected, newTreatmentDetails, newTreatmentDate, newLocationId);

        System.out.println("Medical record updated successfully.");
    }

    /**
     * Deletes a medical record for a victim after confirmation.
     * 
     * @param victim the victim whose medical record will be deleted
     * @throws RuntimeException if the medical record cannot be deleted
     */
    private void deleteMedicalRecordForVictim(DisasterVictim victim) {
        System.out.println();
        System.out.println("----------- Delete Medical Record -----------");

        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsForVictim(victim.getPersonId());

        if (records.isEmpty()) {
            System.out.println("This victim has no medical records.");
            return;
        }

        for (MedicalRecord record : records) {
            printMedicalRecordSummary(record);
        }

        int recordId = readInt("Please enter medical record ID: ");

        MedicalRecord selected = null;
        for (MedicalRecord record : records) {
            if (record.getId() == recordId) {
                selected = record;
                break;
            }
        }

        if (selected == null) {
            System.out.println("No medical record found with that ID.");
            return;
        }

        if (!confirm("Are you sure you want to delete this medical record? (Y/N): ")) {
            System.out.println("Medical record deletion cancelled.");
            return;
        }

        medicalRecordService.deleteMedicalRecord(selected);
        System.out.println("Medical record deleted successfully.");
    }

    /**
     * Soft deletes a selected victim.
     * The victim stays in the database but is hidden from normal use.
     * 
     * @throws RuntimeException if the victim cannot be soft deleted
     */
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

    /**
     * Hard deletes a selected victim.
     * This permanently removes the victim and related records.
     * 
     * @throws RuntimeException if the victim cannot be hard deleted
     */
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

    /**
     * Lets the user choose an active victim by ID.
     * 
     * @return the selected victim, or null if no match is found
     * @throws RuntimeException if the victims cannot be loaded
     */
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

    /**
     * Prints a one-line summary of a victim.
     * 
     * @param victim the victim to display
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
     * Prints a one-line summary of a medical record.
     * 
     * @param record the medical record to display
     */
    private void printMedicalRecordSummary(MedicalRecord record) {
        String locationInfo = record.getLocationId() == null ? "None" : String.valueOf(record.getLocationId());

        System.out.println("ID: " + record.getId() + " | Victim ID: " + record.getVictimId()
                + " | Treatment Date: " + record.getTreatmentDate()
                + " | Location ID: " + locationInfo
                + " | Details: " + record.getTreatmentDetails());
    }

    /**
     * Reads an integer from the user.
     * Keeps asking until the user enters a valid number.
     * 
     * @param prompt the message shown to the user
     * @return the integer entered by the user
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
     * Reads an optional integer from the user.
     * Blank input is allowed and returns null.
     * 
     * @param prompt the message shown to the user
     * @return the integer entered by the user, or null if left blank
     */
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

    /**
     * Reads a date from the user.
     * Keeps asking until the user enters a valid date.
     * 
     * @param prompt the message shown to the user
     * @return the date entered by the user
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
     * Reads a required string from the user.
     * Keeps asking until the user enters a non-blank value.
     * 
     * @param prompt the message shown to the user
     * @return the string entered by the user
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
     * Reads an optional string from the user.
     * 
     * @param prompt the message shown to the user
     * @return the string entered by the user
     */
    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Asks the user to confirm an action with yes or no.
     * 
     * @param prompt the message shown to the user
     * @return true if the user confirms, false otherwise
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

    /**
     * Lets the user choose a gender option from the menu.
     * 
     * @return the selected gender value
     */
    private String chooseGender() {
        while (true) {
            System.out.println();
            System.out.println("Choose gender:");
            System.out.println("1. Man");
            System.out.println("2. Woman");
            System.out.println("3. Boy");
            System.out.println("4. Girl");
            System.out.println("5. Please specify");
            System.out.println("6. Leave blank");

            int choice = readInt("Please choose an option (1-6): ");

            if (choice == 1) {
                return "Man";
            }
            if (choice == 2) {
                return "Woman";
            }
            if (choice == 3) {
                return "Boy";
            }
            if (choice == 4) {
                return "Girl";
            }
            if (choice == 5) {
                return "Please specify";
            }
            if (choice == 6) {
                return "";
            }

            System.out.println("Invalid option. Please choose 1-6.");
        }
    }
}
