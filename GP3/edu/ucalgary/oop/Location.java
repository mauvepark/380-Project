/*
Copyright Ann Barcomb and Khawla Shnaikat, 2024-2025
Licensed under GPL v3
See LICENSE.txt for more information.
*/

package edu.ucalgary.oop;

public class Location {
    private String name;
    private String address;
    private DisasterVictim[] occupants;
    private Supply[] supplies; 

    // Constructor
    public Location(String name, String address) {
        this.name = name;
        this.address = address;
        this.occupants = new DisasterVictim[0];
        this.supplies = new Supply[0]; 
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DisasterVictim[] getOccupants() {
        return occupants; 
    }

    public void setOccupants(DisasterVictim[] occupants) {
        // Using clone() for defensive copying: creates a new array with the same elements
        // This prevents external code from modifying the internal array structure
        // Note: This is a SHALLOW copy - the DisasterVictim objects themselves are shared
        this.occupants = occupants != null ? occupants.clone() : new DisasterVictim[0];
    }

    public Supply[] getSupplies() {
        return supplies; 
    }

    public void setSupplies(Supply[] supplies) {
        // Using clone() for defensive copying: creates a new array with the same elements
        // This prevents external code from modifying the internal array structure
        // Note: This is a SHALLOW copy - the Supply objects themselves are shared
        this.supplies = supplies != null ? supplies.clone() : new Supply[0];
    }

    public void addOccupant(DisasterVictim occupant) {
        if (occupant == null) {
            throw new IllegalArgumentException("Occupant cannot be null");
        }
        
        // Create new array with size + 1
        DisasterVictim[] newOccupants = new DisasterVictim[occupants.length + 1];
        
        // Copy existing occupants
        System.arraycopy(occupants, 0, newOccupants, 0, occupants.length);
        
        // Add new occupant
        newOccupants[occupants.length] = occupant;
        
        // Replace old array
        this.occupants = newOccupants;
    }

    public void removeOccupant(DisasterVictim occupant) throws IllegalArgumentException {
        if (occupant == null) {
            throw new IllegalArgumentException("Occupant cannot be null");
        }
        
        // Find the occupant
        int index = -1;
        for (int i = 0; i < occupants.length; i++) {
            if (occupants[i].equals(occupant)) {
                index = i;
                break;
            }
        }
        
        // If not found, throw exception
        if (index == -1) {
            throw new IllegalArgumentException("Occupant not found in location");
        }
        
        // Create new array with size - 1
        DisasterVictim[] newOccupants = new DisasterVictim[occupants.length - 1];
        
        // Copy elements before the index
        System.arraycopy(occupants, 0, newOccupants, 0, index);
        
        // Copy elements after the index
        System.arraycopy(occupants, index + 1, newOccupants, index, occupants.length - index - 1);
        
        // Replace old array
        this.occupants = newOccupants;
    }

    public void addSupply(Supply supply) {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }
        
        // Create new array with size + 1
        Supply[] newSupplies = new Supply[supplies.length + 1];
        
        // Copy existing supplies
        System.arraycopy(supplies, 0, newSupplies, 0, supplies.length);
        
        // Add new supply
        newSupplies[supplies.length] = supply;
        
        // Replace old array
        this.supplies = newSupplies;
    }

    public void removeSupply(Supply supply) throws IllegalArgumentException {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }
        
        // Find the supply
        int index = -1;
        for (int i = 0; i < supplies.length; i++) {
            if (supplies[i].equals(supply)) {
                index = i;
                break;
            }
        }
        
        // If not found, throw exception
        if (index == -1) {
            throw new IllegalArgumentException("Supply not found in location");
        }
        
        // Create new array with size - 1
        Supply[] newSupplies = new Supply[supplies.length - 1];
        
        // Copy elements before the index
        System.arraycopy(supplies, 0, newSupplies, 0, index);
        
        // Copy elements after the index
        System.arraycopy(supplies, index + 1, newSupplies, index, supplies.length - index - 1);
        
        // Replace old array
        this.supplies = newSupplies;
    }
}
