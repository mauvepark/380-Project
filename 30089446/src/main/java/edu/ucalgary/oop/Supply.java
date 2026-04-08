package edu.ucalgary.oop;

import java.time.LocalDate;

public class Supply {
    private int id;
    private String supplyType;
    private Integer locationId;
    private Integer victimId;
    private LocalDate expiryDate;
    private LocalDate allocationDate;
    private String description;

    // constructors
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

    // constructor no ID
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

    // NEW: methods for expiry tracking
    public boolean isPerishable() {
        return expiryDate != null;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isAllocated() {
        return victimId != null;
    }
}