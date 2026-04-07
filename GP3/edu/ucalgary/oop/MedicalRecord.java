/*
Copyright Ann Barcomb and Khawla Shnaikat, 2024-2025
Licensed under GPL v3
See LICENSE.txt for more information.
*/

package edu.ucalgary.oop;

import java.time.LocalDate;

public class MedicalRecord {
    private Location location;
    private String treatmentDetails;
    private LocalDate dateOfTreatment; 

    public MedicalRecord(Location location, String treatmentDetails, LocalDate dateOfTreatment) throws IllegalArgumentException {
        setLocation(location);
        this.treatmentDetails = treatmentDetails;
        
        if (dateOfTreatment == null) {
            throw new IllegalArgumentException("Date of treatment cannot be null");
        }
        if (dateOfTreatment.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of treatment cannot be in the future");
        }
        this.dateOfTreatment = dateOfTreatment;
    }

    // Getter and setter for location
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // Getter for treatmentDetails
    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    // Setter for treatmentDetails
    public void setTreatmentDetails(String treatmentDetails) throws IllegalArgumentException {
        this.treatmentDetails = treatmentDetails;
    }

    public LocalDate getDateOfTreatment() {
        return dateOfTreatment;
    }

    public void setDateOfTreatment(LocalDate dateOfTreatment) throws IllegalArgumentException {
        if (dateOfTreatment == null) {
            throw new IllegalArgumentException("Date of treatment cannot be null");
        }
        if (dateOfTreatment.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of treatment cannot be in the future");
        }
        this.dateOfTreatment = dateOfTreatment;
    }
}
