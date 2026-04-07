/*
Copyright Ann Barcomb and Khawla Shnaikat, 2024-2025
Licensed under GPL v3
See LICENSE.txt for more information.
*/

package edu.ucalgary.oop;

public class Supply {
    private String type;
    private int quantity;

    public Supply(String type, int quantity) throws IllegalArgumentException {
        this.type = type;
        setQuantity(quantity); // Use setter for validation
    }

    public void setType(String type) { this.type = type; }
    
    public void setQuantity(int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
    
    public String getType() { return this.type; }
    public int getQuantity() { return this.quantity; }
}
