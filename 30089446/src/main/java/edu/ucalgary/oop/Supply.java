/*
Copyright Ann Barcomb and Khawla Shnaikat, 2024-2025
Licensed under GPL v3
See LICENSE.txt for more information.
*/

package edu.ucalgary.oop;
import java.time.LocalDate;

public class Supply {
    private String type;
    private int quantity;
    private int id;
    private String supplyType;
    private Location location;
    private Integer victimId;
    private LocalDate expiryDate;
    private LocalDate allocationDate;
    private String description;

    public Supply(String type, int quantity) throws IllegalArgumentException {
        this.type = type;
        setQuantity(quantity); // Use setter for validation
    }

    // setters and getters
    public void setType(String type) {
        this.type = type;
    }
    
    public void setQuantity(int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
    
    public String getType() {
        return this.type;
    }

    public int getQuantity() {
        return this.quantity;
    }

        boolean isPerishable() {
        return expiryDate != null;
    }

    boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(expiryDate);
    }

    boolean isAllocated() {
        return victimId != null;
    }

    boolean isAvailable() {
        return !isExpired() && !isAllocated();
    }
}
