package edu.ucalgary.oop;

import java.util.*;

/**
 * Controller class to manage user interactions related to locations. 
 * This class provides a menu-driven interface for viewing, adding, updating, and deleting locations.
 * It interacts with the LocationService to perform the necessary operations on the location data.
 */
public class LocationController {
    private final LocationService service;
    private final Scanner scanner;

    /**
     * Constructor for LocationController. Initializes the LocationService and sets up the Scanner for user input.
     * 
     * @param scanner
     */
    public LocationController(Scanner scanner) {
        this.service = new LocationService();
        this.scanner = scanner;
    }

    /**
     * Displays the location management menu and handles user input to perform various operations on locations,
     * such as viewing all locations, adding a new location, updating an existing location, and deleting a location. 
     * The menu continues to display until the user chooses to go back to the main menu
     */
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("----------- Location Management -----------");
            System.out.println("1. View All Locations");
            System.out.println("2. Add Location");
            System.out.println("3. Update Location");
            System.out.println("4. Delete Location");
            System.out.println("5. Back");

            int choice = readInt("Please choose an option (1-5): ");

            switch (choice) {
                case 1:
                    viewAllLocations();
                    break;
                case 2:
                    addLocation();
                    break;
                case 3:
                    updateLocation();
                    break;
                case 4:
                    deleteLocation();
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
     * Displays a list of all locations. 
     * If no locations are found, it informs the user accordingly. 
     * Each location is displayed with its ID, name, and address.
     */
    private void viewAllLocations() {
        List<Location> locations = service.getAllLocations();

        if (locations.isEmpty()) {
            System.out.println("No locations found.");
            return;
        }

        System.out.println();
        System.out.println("Locations:");
        for (Location location : locations) {
            System.out.println(location.getId() + ". " + location.getName() + " | " + location.getAddress());
        }
    }

    /**
     * Prompts the user to enter details for a new location (name and address) and attempts to add it using the LocationService.
     * If the location is added successfully, it confirms the addition and displays the new location's ID.
     * 
     * @throws RuntimeException if there is an error adding the location, such as invalid input or a database error.
     */
    private void addLocation() {
        try {
            System.out.println();
            String name = readNonBlankString("Enter location name: ");
            String address = readNonBlankString("Enter location address: ");

            Location location = service.addLocation(name, address);

            System.out.println("Location added successfully with ID " + location.getId() + ".");
        } catch (RuntimeException e) {
            System.out.println("Could not add location: " + e.getMessage());
        }
    }

    /**
     * Allows the user to update an existing location by first displaying a list of all locations and prompting the user to select one by ID.
     * Once a location is selected, the user is prompted to enter a new name and address for the location.
     * 
     * @throws RuntimeException if there is an error updating the location, such as invalid input, location not found, or a database error.
     */
    private void updateLocation() {
        try {
            List<Location> locations = service.getAllLocations();

            if (locations.isEmpty()) {
                System.out.println("No locations available to update.");
                return;
            }

            System.out.println();
            System.out.println("Please select a location to update:");
            for (Location location : locations) {
                System.out.println(location.getId() + ". " + location.getName() + " | " + location.getAddress());
            }

            int locationId = readInt("Enter location ID: ");
            Location location = service.getLocationById(locationId);

            if (location == null) {
                System.out.println("Location not found.");
                return;
            }

            String newName = readNonBlankString("Enter new location name: ");
            String newAddress = readNonBlankString("Enter new location address: ");

            service.updateLocation(location, newName, newAddress);

            System.out.println("Location updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not update location: " + e.getMessage());
        }
    }

    /**
     * Allows the user to delete an existing location by first displaying a list of all locations and prompting the user to select one by ID.
     * Once a location is selected, the user is asked to confirm the deletion by typing "
     * 
     * @throws RuntimeException if there is an error deleting the location, such as invalid input, location not found, or a database error.
     */
    private void deleteLocation() {
        try {
            List<Location> locations = service.getAllLocations();

            if (locations.isEmpty()) {
                System.out.println("No locations available to delete.");
                return;
            }

            System.out.println();
            System.out.println("Select a location to delete:");
            for (Location location : locations) {
                System.out.println(location.getId() + ". " + location.getName()
                        + " | " + location.getAddress());
            }

            int locationId = readInt("Enter location ID: ");
            Location location = service.getLocationById(locationId);

            if (location == null) {
                System.out.println("Location not found.");
                return;
            }

            String confirm = readNonBlankString(
                "Type DELETE to confirm deletion of " + location.getName() + ": "
            );

            if (!confirm.equals("DELETE")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            service.deleteLocation(location);
            System.out.println("Location deleted successfully.");
        } catch (RuntimeException e) {
            System.out.println("Could not delete location: " + e.getMessage());
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
     * Utility method to read a non-blank string from user input with a prompt.
     * 
     * @param prompt the message to display to the user when asking for input
     * @return the non-blank string entered by the user
     */
    private String readNonBlankString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be blank.");
        }
    }
}