package edu.ucalgary.oop;

import java.time.LocalDate;

/**
 * Represents a medical record for a disaster victim, containing details about the treatment they received, 
 * the date of treatment, and the location where the treatment was provided. 
 * Each medical record is associated with a specific victim and may optionally be linked to a location where the treatment occurred. 
 * The class includes validation to ensure that all required fields are properly set and that the data is consistent 
 * (e.g., treatment date cannot be in the future).
 */
public class MedicalRecord {
    private int id;
    private int victimId;
    private String treatmentDetails;
    private LocalDate treatmentDate;
    private Integer locationId;
    private DisasterVictim victim;

    /**
     * Constructor for creating a MedicalRecord with a specified ID (used when loading from the database).
     * 
     * @param id the unique identifier for the medical record (must be non-negative)
     * @param victimId the ID of the victim associated with this medical record (must be positive)
     * @param treatmentDetails details about the treatment provided (cannot be null or blank)
     * @param treatmentDate the date when the treatment was provided (cannot be null or in the future)
     * @param locationId the ID of the location where the treatment was provided (can be null if not applicable)
     */
    public MedicalRecord(int id, int victimId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
        setId(id);
        setVictimId(victimId);
        setTreatmentDetails(treatmentDetails);
        setTreatmentDate(treatmentDate);
        setLocationId(locationId);
    }

    /**
     * Constructor for creating a MedicalRecord without specifying an ID (used when creating a new record to be inserted into the database).
     * 
     * @param victimId the ID of the victim associated with this medical record (must be positive)
     * @param treatmentDetails details about the treatment provided (cannot be null or blank)
     * @param treatmentDate the date when the treatment was provided (cannot be null or in the future)
     * @param locationId the ID of the location where the treatment was provided (can be null if not applicable)
     */
    public MedicalRecord(int victimId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
        setVictimId(victimId);
        setTreatmentDetails(treatmentDetails);
        setTreatmentDate(treatmentDate);
        setLocationId(locationId);
    }

    // getters and setters
    public DisasterVictim getVictim() {
        return victim;
    }

    public void setVictim(DisasterVictim victim) {
        this.victim = victim;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Medical record ID cannot be negative.");
        }
        this.id = id;
    }

    public int getVictimId() {
        return victimId;
    }

    public void setVictimId(int victimId) {
        if (victimId <= 0) {
            throw new IllegalArgumentException("Victim ID must be positive.");
        }
        this.victimId = victimId;
    }

    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    public void setTreatmentDetails(String treatmentDetails) {
        if (treatmentDetails == null || treatmentDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment details cannot be null or blank.");
        }
        this.treatmentDetails = treatmentDetails.trim();
    }

    public LocalDate getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(LocalDate treatmentDate) {
        if (treatmentDate == null) {
            throw new IllegalArgumentException("Treatment date cannot be null.");
        }
        if (treatmentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Treatment date cannot be in the future.");
        }
        this.treatmentDate = treatmentDate;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}