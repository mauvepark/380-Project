package edu.ucalgary.oop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class InquiryController {
    private final InquiryService service;
    private final Scanner scanner;

    // constructor
    public InquiryController() {
        this.service = new InquiryService();
        this.scanner = new Scanner(System.in);
    }

    // inquiry menu
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("----------- Inquiry Management Menu -----------");
            System.out.println("1. View all inquiries");
            System.out.println("2. View inquiries by inquirer");
            System.out.println("3. View inquiries by subject person");
            System.out.println("4. Add inquiry");
            System.out.println("5. Update inquiry details");
            System.out.println("6. Update inquiry subject");
            System.out.println("7. Delete inquiry");
            System.out.println("8. Back");

            int choice = readInt("Please choose an option (1-8): ");

            try {
                switch (choice) {
                    case 1:
                        viewAllInquiries();
                        break;
                    case 2:
                        viewInquiriesByInquirer();
                        break;
                    case 3:
                        viewInquiriesBySubjectPerson();
                        break;
                    case 4:
                        addInquiry();
                        break;
                    case 5:
                        updateInquiryDetails();
                        break;
                    case 6:
                        updateInquirySubject();
                        break;
                    case 7:
                        deleteInquiry();
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

    // view all inquiries
    public void viewAllInquiries() {
        List<Inquiry> inquiries = service.getAllInquiries();

        System.out.println();
        System.out.println("----------- All Inquiries -----------");

        if (inquiries.isEmpty()) {
            System.out.println("No inquiries found.");
            return;
        }

        for (Inquiry inquiry : inquiries) {
            printInquirySummary(inquiry);
        }
    }

    // view inquiries by inquirer
    public void viewInquiriesByInquirer() {
        System.out.println();
        System.out.println("----------- Inquiries By Inquirer -----------");

        int inquirerId = readInt("Please enter inquirer person ID: ");
        List<Inquiry> inquiries = service.getInquiriesByInquirer(inquirerId);

        if (inquiries.isEmpty()) {
            System.out.println("No inquiries found for this inquirer.");
            return;
        }

        for (Inquiry inquiry : inquiries) {
            printInquirySummary(inquiry);
        }
    }

    // view inquiries by subject person
    public void viewInquiriesBySubjectPerson() {
        System.out.println();
        System.out.println("----------- Inquiries By Subject Person -----------");

        int subjectPersonId = readInt("Please enter subject person ID: ");
        List<Inquiry> inquiries = service.getInquiriesBySubjectPerson(subjectPersonId);

        if (inquiries.isEmpty()) {
            System.out.println("No inquiries found for this subject person.");
            return;
        }

        for (Inquiry inquiry : inquiries) {
            printInquirySummary(inquiry);
        }
    }

    // add inquiry
    public void addInquiry() {
        System.out.println();
        System.out.println("----------- Add Inquiry -----------");

        int inquirerId = readInt("Inquirer person ID: ");
        Integer subjectPersonId = readOptionalInt("Subject person ID (press enter to leave blank): ");
        String details = readRequiredString("Inquiry details: ");

        Person inquirer = new Person(inquirerId, "", "", null);
        Person subjectPerson = null;

        if (subjectPersonId != null) {
            subjectPerson = new Person(subjectPersonId, "", "", null);
        }

        Inquiry inquiry = service.addInquiry(inquirer, subjectPerson, LocalDateTime.now(),details);

        System.out.println("Inquiry added successfully.");
        printInquirySummary(inquiry);
    }

    // update inquiry details
    public void updateInquiryDetails() {
        System.out.println();
        System.out.println("----------- Update Inquiry Details -----------");

        Inquiry inquiry = selectInquiry();
        if (inquiry == null) {
            return;
        }

        String newDetails = readRequiredString("New inquiry details: ");
        service.updateInquiryDetails(inquiry, newDetails);

        System.out.println("Inquiry updated successfully.");
    }

    // update inquiry subject
    public void updateInquirySubject() {
        System.out.println();
        System.out.println("----------- Update Inquiry Subject -----------");

        Inquiry inquiry = selectInquiry();
        if (inquiry == null) {
            return;
        }

        Integer subjectPersonId = readOptionalInt("New subject person ID (press enter to clear): ");
        Person newSubjectPerson = null;

        if (subjectPersonId != null) {
            newSubjectPerson = new Person(subjectPersonId, "", "", null);
        }

        service.updateInquirySubject(inquiry, newSubjectPerson);

        System.out.println("Inquiry subject updated successfully.");
    }

    // delete inquiry
    public void deleteInquiry() {
        System.out.println();
        System.out.println("----------- Delete Inquiry -----------");
        System.out.println("WARNING: Deleting an inquiry permanently removes it from the database.");

        Inquiry inquiry = selectInquiry();
        if (inquiry == null) {
            return;
        }

        if (!confirm("Are you sure you want to delete this inquiry? (Y/N): ")) {
            System.out.println("Delete cancelled.");
            return;
        }

        service.deleteInquiry(inquiry);
        System.out.println("Inquiry deleted successfully.");


    }

    // select inquiry by id
    private Inquiry selectInquiry() {
        List<Inquiry> inquiries = service.getAllInquiries();

        if (inquiries.isEmpty()) {
            System.out.println("No inquiries found.");
            return null;
        }

        for (Inquiry inquiry : inquiries) {
            printInquirySummary(inquiry);
        }

        int inquiryId = readInt("Please enter inquiry ID: ");
        Inquiry inquiry = service.getInquiryById(inquiryId);

        if (inquiry == null) {
            System.out.println("No inquiry found with that ID.");
            return null;
        }

        return inquiry;
    }

    // print inquiry info
    private void printInquirySummary(Inquiry inquiry) {
        String inquirerName = formatPersonName(inquiry.getInquirer());
        String subjectName = formatPersonName(inquiry.getSubjectPerson());

        System.out.println("ID: " + inquiry.getId() + " | Inquirer: " + inquirerName + " | Subject: " + subjectName 
                            + " | Date: " + inquiry.getInquiryDate() + " | Details: " + inquiry.getDetails());
    }

    private String formatPersonName(Person person) {
        if (person == null) {
            return "Unknown";
        }

        String firstName = person.getFirstName();
        String lastName = person.getLastName();

        if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
            return "Person ID " + person.getId();
        }

        if (lastName == null || lastName.isBlank()) {
            return firstName;
        }

        return firstName + " " + lastName;
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