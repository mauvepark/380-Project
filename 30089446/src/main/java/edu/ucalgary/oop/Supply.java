package edu.ucalgary.oop;

import java.time.LocalDate;

/**
 * Represents a supply item in the system.
 * A supply can be stored at a location, assigned to a victim, and may have an expiry date.
 */
public class Supply {
    private int id;
    private String supplyType;
    private Integer locationId;
    private Integer victimId;
    private LocalDate expiryDate;
    private LocalDate allocationDate;
    private String description;
    private Location location;

    /**
     * Creates a supply with a known ID.
     *
     * @param id the supply ID
     * @param supplyType the type of supply
     * @param locationId the ID of the location storing the supply
     * @param victimId the ID of the victim the supply is assigned to
     * @param expiryDate the expiry date of the supply
     * @param allocationDate the date the supply was assigned
     * @param description extra details about the supply
     */
    public Supply(int id, String supplyType, Integer locationId, Integer victimId, 
                  LocalDate expiryDate, LocalDate allocationDate, String description) {
        setSupplyType(supplyType); // for validation
        this.id = id;
        this.locationId = locationId;
        this.victimId = victimId;
        this.expiryDate = expiryDate;
        this.allocationDate = allocationDate;
        this.description = description;
    }

    /**
     * Creates a supply without an ID.
     *
     * @param supplyType the type of supply
     * @param locationId the ID of the location storing the supply
     * @param victimId the ID of the victim the supply is assigned to
     * @param expiryDate the expiry date of the supply
     * @param allocationDate the date the supply was assigned
     * @param description extra details about the supply
     */
    public Supply(String supplyType, Integer locationId, Integer victimId,
                  LocalDate expiryDate, LocalDate allocationDate, String description) {
        setSupplyType(supplyType); // for validation
        this.locationId = locationId;
        this.victimId = victimId;
        this.expiryDate = expiryDate;
        this.allocationDate = allocationDate;
        this.description = description;
    }

    // getters and setters
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Supply id cannot be negative.");
        }
        this.id = id;
    }

    public String getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(String supplyType) {
        if (supplyType == null || supplyType.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply type cannot be null or empty.");
        }
        this.supplyType = supplyType;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getVictimId() {
        return victimId;
    }

    public void setVictimId(Integer victimId) {
        this.victimId = victimId;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks if the supply is perishable.
     *
     * @return true if the supply has an expiry date, false otherwise
     */
    public boolean isPerishable() {
        return expiryDate != null;
    }

    /**
     * Checks if the supply is expired.
     *
     * @return true if the expiry date is before today, false otherwise
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    /**
     * Checks if the supply is currently allocated to a victim.
     *
     * @return true if the supply has a victim ID, false otherwise
     */
    public boolean isAllocated() {
        return victimId != null;
    }
}
