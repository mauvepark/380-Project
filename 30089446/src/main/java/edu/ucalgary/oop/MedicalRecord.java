package edu.ucalgary.oop;

import java.time.LocalDate;

public class MedicalRecord {
    private int id;
    private int victimId;
    private String treatmentDetails;
    private LocalDate treatmentDate;
    private Integer locationId;

    // constructors
    public MedicalRecord(int id, int victimId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
        setId(id);
        setVictimId(victimId);
        setTreatmentDetails(treatmentDetails);
        setTreatmentDate(treatmentDate);
        setLocationId(locationId);
    }

    public MedicalRecord(int victimId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
        setVictimId(victimId);
        setTreatmentDetails(treatmentDetails);
        setTreatmentDate(treatmentDate);
        setLocationId(locationId);
    }

    // getters and setters
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