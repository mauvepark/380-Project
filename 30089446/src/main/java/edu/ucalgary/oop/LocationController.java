package edu.ucalgary.oop;

import java.util.*;

public class LocationController {
    private final LocationService service;
    private final Scanner scanner;

    // constructor
    public LocationController() {
        this.service = new LocationService();
        this.scanner = new Scanner(System.in);
    }

    // location menu
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

    // view all locations
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

    // add location
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

    // update existing location
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

    // delete location
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