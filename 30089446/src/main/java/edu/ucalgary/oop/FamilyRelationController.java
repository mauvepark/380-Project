package edu.ucalgary.oop;

import java.util.List;
import java.util.Scanner;

/** 
 * Controller class to manage the family relationships between disaster victims.
 * This class provides a menu-driven interface for users to view, add, update, and delete family relationships.
 * It interacts with the FamilyRelationService to perform the necessary operations and handles user input and output.
 */
public class FamilyRelationController {
    private final FamilyRelationService service;
    private final Scanner scanner;

    // constructor
    public FamilyRelationController(Scanner scanner) {
        this.service = new FamilyRelationService();
        this.scanner = scanner;
    }

    /**
     * Displays the family relationship management menu and handles user input to perform various operations related to family relationships.
     * The menu allows users to view all family relationships, view relationships for a specific person,
     * add a new family relationship, update the relationship type of an existing relationship, and delete a relationship.
     * 
     * @throws IllegalArgumentException if the user inputs invalid data (e.g., non-integer where an integer is expected, blank relationship type).
     * @throws RuntimeException if there is an error performing the requested operation.
     */
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("----------- Family Relationship Management Menu -----------");
            System.out.println("1. View all family relationships");
            System.out.println("2. View family relationships for a person");
            System.out.println("3. Add a family relationship");
            System.out.println("4. Update a family relationship type");
            System.out.println("5. Delete a family relationship");
            System.out.println("6. Back");

            int choice = readInt("Please choose an option (1-6): ");

            try {
                switch (choice) {
                    case 1:
                        viewAllFamilyRelations();
                        break;
                    case 2:
                        viewFamilyRelationsForPerson();
                        break;
                    case 3:
                        addFamilyRelation();
                        break;
                    case 4:
                        updateRelationshipType();
                        break;
                    case 5:
                        deleteFamilyRelation();
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
     * Retrieves and displays all family relationships from the database. Each relationship is printed in a user-friendly format showing the IDs and names of the two people involved and the type of relationship.
     * If no family relationships are found, a message is displayed indicating that there are no relationships to show.
     */
    public void viewAllFamilyRelations() {
        List<FamilyRelation> relations = service.getAllFamilyRelations();

        System.out.println();
        System.out.println("----------- All Family Relationships -----------");

        if (relations.isEmpty()) {
            System.out.println("No family relationships found.");
            return;
        }

        for (FamilyRelation relation : relations) {
            printFamilyRelation(relation);
        }
    }

    /**
     * Prompts the user to enter a person ID and retrieves all family relationships involving that person from the database.
     * Each relationship is printed in a user-friendly format showing the IDs and names of the two people involved and the type of relationship.
     * If no family relationships are found for the given person ID, a message is displayed indicating that there are no relationships to show.
     */
    public void viewFamilyRelationsForPerson() {
        System.out.println();
        System.out.println("----------- Family Relationships For Person -----------");

        int personId = readInt("Please enter person ID: ");
        List<FamilyRelation> relations = service.getFamilyRelationsForPerson(personId);

        if (relations.isEmpty()) {
            System.out.println("No family relationships found for this person.");
            return;
        }

        for (FamilyRelation relation : relations) {
            printFamilyRelation(relation);
        }
    }

    /**
     * Prompts the user to enter the details of a new family relationship (IDs of the two people and the relationship type) and adds it to the database.
     * The user is required to enter valid integer IDs and a non-blank relationship type.
     * After successfully adding the relationship, a confirmation message is displayed along with the details of the newly added relationship.
     */
    public void addFamilyRelation() {
        System.out.println();
        System.out.println("----------- Add Family Relationship -----------");

        int personOneId = readInt("Person one ID: ");
        int personTwoId = readInt("Person two ID: ");
        String relationshipType = readRequiredString("Relationship type: ");

        Person personOne = new Person(personOneId, "", "", null);
        Person personTwo = new Person(personTwoId, "", "", null);

        FamilyRelation relation = service.addFamilyRelation(personOne, relationshipType, personTwo);

        System.out.println("Family relationship added successfully.");
        printFamilyRelation(relation);
    }

    /**
     * Prompts the user to select an existing family relationship by ID and then enter a new relationship type to update it in the database.
     * The user is required to enter a valid integer ID for the relationship and a non-blank new relationship type.
     * After successfully updating the relationship, a confirmation message is displayed along with the details of the 
     * updated relationship showing the old and new relationship types. 
     */
    public void updateRelationshipType() {
        System.out.println();
        System.out.println("----------- Update Relationship Type -----------");

        FamilyRelation relation = selectFamilyRelation();
        if (relation == null) {
            return;
        }

        String newRelationshipType = readRequiredString("New relationship type: ");
        service.updateRelationshipType(relation, newRelationshipType);

        System.out.println("Family relationship updated successfully.");
    }

    /**
     * Prompts the user to select an existing family relationship by ID and then confirms if they want to delete it. If confirmed, the relationship is removed from the database.
     * The user is required to enter a valid integer ID for the relationship and confirm the deletion with a yes/no response.
     * After successfully deleting the relationship, a confirmation message is displayed.
     */
    public void deleteFamilyRelation() {
        System.out.println();
        System.out.println("----------- Delete Family Relationship -----------");
        System.out.println("WARNING: Deleting a family relationship permanently removes it from the database.");

        FamilyRelation relation = selectFamilyRelation();
        if (relation == null) {
            return;
        }

        if (!confirm("Are you sure you want to delete this family relationship? (Y/N): ")) {
            System.out.println("Delete cancelled.");
            return;
        }

        service.deleteFamilyRelation(relation);
        System.out.println("Family relationship deleted successfully.");
    }

    /**
     * Prompts the user to select an existing family relationship by displaying all relationships and asking for the relationship ID.
     * The user is required to enter a valid integer ID corresponding to an existing family relationship.
     * 
     * @return the selected family relationship, or null if none is selected
     */
    private FamilyRelation selectFamilyRelation() {
        List<FamilyRelation> relations = service.getAllFamilyRelations();

        if (relations.isEmpty()) {
            System.out.println("No family relationships found.");
            return null;
        }

        for (FamilyRelation relation : relations) {
            printFamilyRelation(relation);
        }

        int relationId = readInt("Please enter relationship ID: ");
        FamilyRelation relation = service.getFamilyRelationById(relationId);

        if (relation == null) {
            System.out.println("No family relationship found with that ID.");
            return null;
        }

        return relation;
    }

    /** 
     * Prints the details of a family relationship in a user-friendly format showing the IDs and names of the 
     * two people involved and the type of relationship.
     * If a person's name is not available, their ID is shown instead. If both names are unavailable, 
     * "Unknown" is displayed for that person.
     */
    private void printFamilyRelation(FamilyRelation relation) {
        String personOneName = formatPersonName(relation.getPersonOne());
        String personTwoName = formatPersonName(relation.getPersonTwo());

        System.out.println("ID: " + relation.getId() + " | Person One: " + personOneName + " | Type: " + relation.getRelationshipType() + " | Person Two: " + personTwoName);
    }

    /**
     * Formats a person's name for display. If the person's first and last name are available, they are combined. 
     * If only one is available, it is used. If neither is available, "Unknown" is returned along with the person's ID.
     * 
     * @param person the person whose name is to be formatted
     * @return a string representing the person's name for display purposes
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
     * Reads an integer input from the user with the given prompt. 
     * If the user enters an invalid integer, they will be prompted again until a valid integer is entered.
     * 
     * @param prompt The message to display to the user when asking for input.
     * @return the integer value entered by the user.
     * @throws RuntimeException if the user inputs an invalid integer.
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
     * Reads a non-blank string input from the user with the given prompt. 
     * If the user enters a blank string, they will be prompted again until a non-blank string is entered.
     * 
     * @param prompt The message to display to the user when asking for input.
     * @return the non-blank string entered by the user.
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