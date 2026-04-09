package edu.ucalgary.oop;

import java.util.List;
import java.util.Scanner;

public class FamilyRelationController {
    private final FamilyRelationService service;
    private final Scanner scanner;

    // constructor
    public FamilyRelationController() {
        this.service = new FamilyRelationService();
        this.scanner = new Scanner(System.in);
    }

    // family relationships menu
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

    // view all
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

    // view for one person
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

    // add relationship
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

    // update relationship
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

    // delete relation
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

    // select by id
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

    // print 
    private void printFamilyRelation(FamilyRelation relation) {
        String personOneName = formatPersonName(relation.getPersonOne());
        String personTwoName = formatPersonName(relation.getPersonTwo());

        System.out.println("ID: " + relation.getId() + " | Person One: " + personOneName + " | Type: " + relation.getRelationshipType() + " | Person Two: " + personTwoName);
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