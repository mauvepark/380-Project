package edu.ucalgary.oop;

import java.time.*;
import java.util.*;

/**
 * Controller class for managing supplies.
 * This class lets the user view, add, update, allocate, unallocate, and delete supplies through a menu.
 */
public class SupplyController {
    private final SupplyService service;
    private final Scanner scanner;

    /**
     * Constructor for SupplyController.
     * Sets up the supply service and scanner for user input.
     * 
     * @param scanner
     */
    public SupplyController(Scanner scanner) {
        this.service = new SupplyService();
        this.scanner = scanner;
    }

    /**
     * Displays the supply management menu and handles user choices.
     * The menu keeps running until the user chooses to go back.
     * 
     * @throws RuntimeException if an unexpected error happens during a menu action
     */
    public void menu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("----------- Supply Management Menu -----------");
            System.out.println("1. View all supplies");
            System.out.println("2. View supplies by location");
            System.out.println("3. View available inventory");
            System.out.println("4. View expired supplies by location");
            System.out.println("5. Add a supply");
            System.out.println("6. Update supply");
            System.out.println("7. Allocate supply");
            System.out.println("8. Unallocate supply");
            System.out.println("9. Delete supply");
            System.out.println("10. Back");

            int choice = readInt("Please choose an option (1-10): ");

            try {
                switch (choice) {
                    case 1:
                        viewAllSupplies();
                        break;
                    case 2:
                        viewSuppliesByLocation();
                        break;
                    case 3:
                        viewAvailableInventoryForAllocation();
                        break;
                    case 4:
                        viewExpiredSuppliesByLocation();
                        break;
                    case 5:
                        addSupply();
                        break;
                    case 6:
                        updateSupply();
                        break;
                    case 7:
                        allocateSupply();
                        break;
                    case 8:
                        unallocateSupply();
                        break;
                    case 9:
                        deleteSupply();
                        break;
                    case 10:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-10.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    /**
     * Displays all supplies in the system.
     * If there are no supplies, the user is informed.
     * 
     * @throws RuntimeException if the supplies cannot be retrieved
     */
    public void viewAllSupplies() {
        List<Supply> supplies = service.getAllSupplies();

        System.out.println();
        System.out.println("----------- All Supplies -----------");

        if (supplies.isEmpty()) {
            System.out.println("No supplies found.");
            return;
        }

        for (Supply supply : supplies) {
            printSupplySummary(supply);
        }
    }

    /**
     * Displays all supplies for a given location.
     * The user is prompted to enter a location ID first.
     * 
     * @throws RuntimeException if the supplies cannot be retrieved for the location
     */
    public void viewSuppliesByLocation() {
        System.out.println();
        System.out.println("----------- Supplies By Location -----------");

        int locationId = readInt("Please enter location ID: ");
        List<Supply> supplies = service.getSuppliesByLocation(locationId);

        if (supplies.isEmpty()) {
            System.out.println("No supplies found for this location.");
            return;
        }

        for (Supply supply : supplies) {
            printSupplySummary(supply);
        }
    }

    /**
     * Displays supplies at a location that can still be allocated.
     * Expired supplies at that location are shown as a warning first.
     * 
     * @throws RuntimeException if the available inventory cannot be retrieved
     */
    public void viewAvailableInventoryForAllocation() {
        System.out.println();
        System.out.println("----------- Available Inventory For Allocation -----------");

        int locationId = readInt("Please enter location ID: ");
        warnExpiredSupplies(locationId);

        List<Supply> supplies = service.getAvailableInventoryForAllocation(locationId);

        if (supplies.isEmpty()) {
            System.out.println("No available supplies found for this location.");
            return;
        }

        for (Supply supply : supplies) {
            printSupplySummary(supply);
        }
    }

    /**
     * Displays expired supplies for a given location.
     * The user is prompted to enter a location ID first.
     * 
     * @throws RuntimeException if the expired supplies cannot be retrieved
     */
    public void viewExpiredSuppliesByLocation() {
        System.out.println();
        System.out.println("----------- Expired Supplies By Location -----------");

        int locationId = readInt("Please enter location ID: ");
        List<Supply> supplies = service.getExpiredSuppliesByLocation(locationId);

        if (supplies.isEmpty()) {
            System.out.println("No expired supplies found for this location.");
            return;
        }

        for (Supply supply : supplies) {
            printSupplySummary(supply);
        }
    }

    /**
     * Prompts the user for supply information and adds a new supply.
     * The user can choose whether the supply is perishable and enter an expiry date if needed.
     * 
     * @throws RuntimeException if the supply cannot be added
     */
    public void addSupply() {
        System.out.println();
        System.out.println("----------- Add Supply -----------");

        Integer locationId = readOptionalInt("Location ID (press enter to leave blank): ");
        if (locationId == null) {
            System.out.println("Location ID is required.");
            return;
        }

        warnExpiredSupplies(locationId);

        String supplyType = readRequiredString("Supply type: ");
        String description = readOptionalString("Description (press enter to leave blank): ");

        System.out.println();
        System.out.println("Is this supply perishable?");
        System.out.println("1. Yes");
        System.out.println("2. No");

        int choice = readInt("Please choose an option (1-2): ");

        LocalDate expiryDate = null;

        if (choice == 1) {
            expiryDate = readDate("Expiry date (format: YYYY-MM-DD): ");
        } else if (choice == 2) {
            expiryDate = null;
        } else {
            System.out.println("Invalid option. Please choose 1 or 2.");
            return;
        }

        Supply supply = new Supply(
            supplyType,
            locationId,
            null,
            expiryDate,
            null,
            description
        );

        Supply savedSupply = service.addSupply(supply);

        System.out.println("Supply added successfully.");
        printSupplySummary(savedSupply);
    }

    /**
     * Updates an existing supply after the user selects one.
     * The user is prompted for the new supply details.
     * 
     * @throws RuntimeException if the supply cannot be updated
     */
    public void updateSupply() {
        System.out.println();
        System.out.println("----------- Update Supply -----------");

        Supply supply = selectSupply();
        if (supply == null) {
            return;
        }

        Integer locationId = readOptionalInt("New location ID (press enter to leave blank): ");
        if (locationId == null) {
            System.out.println("Location ID is required.");
            return;
        }

        warnExpiredSupplies(locationId);

        String supplyType = readRequiredString("New supply type: ");
        String description = readOptionalString("New description (press enter to leave blank): ");

        System.out.println();
        System.out.println("Is this supply perishable?");
        System.out.println("1. Yes");
        System.out.println("2. No");

        int choice = readInt("Please choose an option (1-2): ");

        LocalDate expiryDate = null;

        if (choice == 1) {
            expiryDate = readDate("Expiry date (format: YYYY-MM-DD): ");
        } else if (choice == 2) {
            expiryDate = null;
        } else {
            System.out.println("Invalid option. Please choose 1 or 2.");
            return;
        }

        supply.setLocationId(locationId);
        supply.setSupplyType(supplyType);
        supply.setDescription(description);
        supply.setExpiryDate(expiryDate);

        service.updateSupply(supply);

        System.out.println("Supply updated successfully.");
    }

    /**
     * Allocates an available supply to a victim.
     * The user chooses a location, a supply, and a victim ID.
     * 
     * @throws RuntimeException if the supply cannot be allocated
     */
    public void allocateSupply() {
        System.out.println();
        System.out.println("----------- Allocate Supply -----------");

        int locationId = readInt("Please enter location ID: ");
        warnExpiredSupplies(locationId);

        List<Supply> supplies = service.getAvailableInventoryForAllocation(locationId);

        if (supplies.isEmpty()) {
            System.out.println("No allocatable supplies available at this location.");
            return;
        }

        System.out.println("Available supplies:");
        for (Supply supply : supplies) {
            printSupplySummary(supply);
        }

        int supplyId = readInt("Please enter supply ID: ");
        int victimId = readInt("Please enter victim ID: ");

        service.allocateSupply(supplyId, victimId);

        System.out.println("Supply allocated successfully.");
    }

    /**
     * Removes a supply allocation from a victim.
     * The user first selects a supply to unallocate.
     * 
     * @throws RuntimeException if the supply cannot be unallocated
     */
    public void unallocateSupply() {
        System.out.println();
        System.out.println("----------- Unallocate Supply -----------");

        Supply supply = selectSupply();
        if (supply == null) {
            return;
        }

        if (!supply.isAllocated()) {
            System.out.println("That supply is not currently allocated.");
            return;
        }

        service.unallocateSupply(supply.getId());

        System.out.println("Supply unallocated successfully.");
    }

    /**
     * Deletes a supply from the system after confirmation.
     * This action permanently removes the supply from the database.
     * 
     * @throws RuntimeException if the supply cannot be deleted
     */
    public void deleteSupply() {
        System.out.println();
        System.out.println("----------- Delete Supply -----------");
        System.out.println("WARNING: Deleting a supply permanently removes it from the database.");

        Supply supply = selectSupply();
        if (supply == null) {
            return;
        }

        if (!confirm("Are you sure you want to delete this supply? (Y/N): ")) {
            System.out.println("Delete cancelled.");
            return;
        }

        service.deleteSupply(supply.getId());

        System.out.println("Supply deleted successfully.");
    }

    /**
     * Displays all supplies and lets the user choose one by ID.
     * 
     * @return the selected supply, or null if no matching supply is found
     * @throws RuntimeException if the supplies cannot be retrieved
     */
    private Supply selectSupply() {
        List<Supply> supplies = service.getAllSupplies();

        if (supplies.isEmpty()) {
            System.out.println("No supplies found.");
            return null;
        }

        for (Supply supply : supplies) {
            printSupplySummary(supply);
        }

        int supplyId = readInt("Please enter supply ID: ");

        for (Supply supply : supplies) {
            if (supply.getId() == supplyId) {
                return supply;
            }
        }

        System.out.println("No supply found with that ID. Please try again.");
        return null;
    }

    /**
     * Displays a warning if a location has expired supplies.
     * 
     * @param locationId the location to check
     * @throws RuntimeException if the expired supplies cannot be retrieved
     */
    private void warnExpiredSupplies(int locationId) {
        List<Supply> expiredSupplies = service.getExpiredSuppliesByLocation(locationId);

        if (!expiredSupplies.isEmpty()) {
            System.out.println();
            System.out.println("WARNING: The following expired supplies exist at this location:");
            for (Supply supply : expiredSupplies) {
                printSupplySummary(supply);
            }
        }
    }

    /**
     * Prints a one-line summary of a supply.
     * 
     * @param supply the supply to display
     */
    private void printSupplySummary(Supply supply) {
        String victimId = supply.getVictimId() == null ? "None" : String.valueOf(supply.getVictimId());
        String expiryDate = supply.getExpiryDate() == null ? "N/A" : supply.getExpiryDate().toString();
        String allocationDate = supply.getAllocationDate() == null ? "N/A" : supply.getAllocationDate().toString();
        String description = supply.getDescription();

        if (description == null) {
            description = "";
        }

        System.out.println("ID: " + supply.getId() + " | Type: " + supply.getSupplyType() + " | Location ID: " + supply.getLocationId()
                            + " | Victim ID: " + victimId + " | Expiry Date: " + expiryDate + " | Allocation Date: " + allocationDate
                            + " | Description: " + description);
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
     * Keeps asking until the user enters a valid date in YYYY-MM-DD format.
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
}
