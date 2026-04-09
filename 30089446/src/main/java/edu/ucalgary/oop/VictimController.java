package edu.ucalgary.oop;

import java.time.*;
import java.util.*;

public class VictimController {
    private final VictimService service;
    private final MedicalRecordService medicalRecordService;
    private final Scanner scanner;

    // constructor
    public VictimController() {
        this.service = new VictimService();
        this.medicalRecordService = new MedicalRecordService();
        this.scanner = new Scanner(System.in);
    }

    // victim menu
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("----------- Victim Management Menu -----------");
            System.out.println("1. View active victims");
            System.out.println("2. Add a victim");
            System.out.println("3. Update victim name");
            System.out.println("4. Update victim age");
            System.out.println("5. Manage victim medical records");
            System.out.println("6. Soft delete victim");
            System.out.println("7. Hard delete victim");
            System.out.println("8. Back");

            int choice = readInt("Please choose an option (1-8): ");

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
                        manageVictimMedicalRecords();
                        break;
                    case 6:
                        softDeleteVictim();
                        break;
                    case 7:
                        hardDeleteVictim();
                        break;
                    case 8:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-8.");
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

    // medical record submenu
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

    private void printMedicalRecordSummary(MedicalRecord record) {
        String locationInfo = record.getLocationId() == null ? "None" : String.valueOf(record.getLocationId());

        System.out.println("ID: " + record.getId() + " | Victim ID: " + record.getVictimId()
                + " | Treatment Date: " + record.getTreatmentDate()
                + " | Location ID: " + locationInfo
                + " | Details: " + record.getTreatmentDetails());
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