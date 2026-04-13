package edu.ucalgary.oop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Controller class for managing inquiries in the disaster victim management system. 
 * Provides a menu-driven interface for users to view, add, update, and delete inquiries, 
 * as well as view inquiries by inquirer or subject person. Interacts with the InquiryService 
 * class to perform operations on the database and handles user input validation and error handling.
 */
public class InquiryController {
    private final InquiryService service;
    private final Scanner scanner;

    // constructor
    public InquiryController(Scanner scanner) {
        this.service = new InquiryService();
        this.scanner = scanner;
    }

    /**
     * Displays the inquiry management menu and handles user input to perform various operations related to inquiries, 
     * such as viewing all inquiries, viewing inquiries by inquirer or subject person, adding a new inquiry, updating inquiry 
     * details or subject, and deleting an inquiry. The method continues to display the menu until the user chooses to exit. 
     * It also includes error handling for invalid input and operation failures.
     * 
     * @throws IllegalArgumentException if the user input is invalid (e.g., non-numeric input for IDs, blank details)
     * @throws IllegalStateException if the selected inquiry does not exist when attempting to update or delete
     * @throws RuntimeException if there is an error performing the database operations through the Inquiry
     */
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

    /**
     * Views all inquiries in the system, displaying the ID, inquirer name, subject name, inquiry date, and details for each inquiry.   
     */
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

    /**
     * Views inquiries by a specific inquirer, prompting the user to enter the inquirer's person ID and displaying the ID,
     * inquirer name, subject name, inquiry date, and details for each matching inquiry.    
     */
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

    /**
     * Views inquiries by a specific subject person, prompting the user to enter the subject person's person ID and displaying the ID,
     * inquirer name, subject name, inquiry date, and details for each matching inquiry.    
     */
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

    /**
     * Adds a new inquiry to the system, prompting the user to enter the inquirer's person ID, 
     * subject person's person ID (optional), and inquiry details.    
     */
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

    /**
     * Updates the details of an existing inquiry, prompting the user to select an inquiry and enter new details.    
     */
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

    /**
     * Updates the subject person of an existing inquiry, prompting the user to select an inquiry and enter a new subject person ID.    
     */
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

    /**
     * Deletes an existing inquiry, prompting the user to select an inquiry and confirm the deletion.
     */
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

    /**
     * Selects an inquiry by its ID, prompting the user to enter the inquiry ID and returning the corresponding inquiry object.
     * 
     * @return the selected Inquiry object, or null if no inquiry is found with the entered ID
     */
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

    /**
     * Prints a summary of an inquiry, including the ID, inquirer name, subject name, inquiry date, and details.
     * 
     * @param inquiry the Inquiry object for which to print the summary
     */
    private void printInquirySummary(Inquiry inquiry) {
        String inquirerName = formatPersonName(inquiry.getInquirer());
        String subjectName = formatPersonName(inquiry.getSubjectPerson());

        System.out.println("ID: " + inquiry.getId() + " | Inquirer: " + inquirerName + " | Subject: " + subjectName 
                            + " | Date: " + inquiry.getInquiryDate() + " | Details: " + inquiry.getDetails());
    }

    /**
     * Formats a person's name for display, using their first and last name if available, or their person ID if the name is not available.
     * 
     * @param person the Person object for which to format the name
     * @return a string representing the person's name for display
     */
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

    /**
     * Reads an integer input from the user, prompting with the provided message. Continues to prompt until a valid integer is entered. 
     * 
     * @param prompt the message to display when asking for input
     * @return the integer value entered by the user
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
     * Reads an optional integer input from the user, prompting with the provided message. Continues to prompt until a valid integer is entered or the user leaves it blank.
     * 
     * @param prompt the message to display when asking for input
     * @return the Integer value entered by the user, or null if the user leaves it blank
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
     * Reads a required string input from the user, prompting with the provided message. Continues to prompt until a non-blank string is entered.
     * 
     * @param prompt the message to display when asking for input
     * @return the string value entered by the user
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
     * Prompts the user to confirm an action with a yes or no response. Continues to prompt until the user enters a valid response (Y/N).
     * 
     * @param prompt the message to display when asking for confirmation
     * @return true if the user confirms with Y or yes, false if the user declines with N or no
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