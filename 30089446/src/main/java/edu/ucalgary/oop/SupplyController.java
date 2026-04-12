package edu.ucalgary.oop;

import java.time.*;
import java.util.*;

public class SupplyController {
    private final SupplyService service;
    private final Scanner scanner;

    // constructor
    public SupplyController(Scanner scanner) {
        this.service = new SupplyService();
        this.scanner = scanner;
    }

    // supply menu
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

    // view all supplies
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

    // view supplies by location
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

    // view available inventory for allocation
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

    // view expired supplies by location
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

    // add supply
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

    // update supply
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

    // allocate supply
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

    // unallocate supply
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

    // delete supply
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

    // select supply by id
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

    // warn about expired supplies
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

    // print supply info
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