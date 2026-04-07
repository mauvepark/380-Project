/*
Copyright Ann Barcomb and Khawla Shnaikat, 2024-2025
Licensed under GPL v3
See LICENSE.txt for more information.
*/

package edu.ucalgary.oop;

import java.time.LocalDate;

public class DisasterVictim {
    // NEW: person object
    private Person person;
    private LocalDate dateOfBirth; 
    // NEW: approximate age
    private Integer approximateAge;
    private FamilyRelation[] familyConnections; 
    private MedicalRecord[] medicalRecords; 
    private Supply[] personalBelongings;
    private final LocalDate ENTRY_DATE; 
    private String gender;

    // constructors
    // refactored constructor to use new Person class
    public DisasterVictim(Person person, LocalDate entryDate, LocalDate dateOfBirth) {
        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }

        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        this.person = person;
        this.ENTRY_DATE = entryDate;
        this.dateOfBirth = dateOfBirth;
        this.approximateAge = null;
        this.familyConnections = new FamilyRelation[0];
        this.medicalRecords = new MedicalRecord[0];
        this.personalBelongings = new Supply[0];


    }

    // NEW: constructor for when we only have approximate age
    public DisasterVictim(Person person, LocalDate entryDate, int approximateAge) {
        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }

        if (approximateAge < 0) {
            throw new IllegalArgumentException("Approximate age cannot be negative");
        }

        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        this.person = person;
        this.ENTRY_DATE = entryDate;
        this.approximateAge = approximateAge;
        this.dateOfBirth = null;
        this.familyConnections = new FamilyRelation[0];
        this.medicalRecords = new MedicalRecord[0];
        this.personalBelongings = new Supply[0];
        
    }

    // setters and getters

    // NEW: getters for person and person ID
    public Person getPerson() {
        return person;
    }

    public int getPersonId() {
        return person.getId();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public FamilyRelation[] getFamilyConnections() {
        return familyConnections;
    }

    public MedicalRecord[] getMedicalRecords() {
        return medicalRecords;
    }

    public Supply[] getPersonalBelongings() {
        return this.personalBelongings;
    }

    public void setFamilyConnections(FamilyRelation[] connections) {
        // Using clone() for defensive copying: creates a new array with the same elements
        // This prevents external code from modifying the internal array structure
        // Note: This is a SHALLOW copy - the FamilyRelation objects themselves are shared
        this.familyConnections = connections != null ? connections.clone() : new FamilyRelation[0];
    }

    public void setMedicalRecords(MedicalRecord[] records) {
        // Using clone() for defensive copying: creates a new array with the same elements
        // This prevents external code from modifying the internal array structure
        // Note: This is a SHALLOW copy - the MedicalRecord objects themselves are shared
        this.medicalRecords = records != null ? records.clone() : new MedicalRecord[0];
    }

    public void setPersonalBelongings(Supply[] belongings) {
        // Using clone() for defensive copying: creates a new array with the same elements
        // This prevents external code from modifying the internal array structure
        // Note: This is a SHALLOW copy - the Supply objects themselves are shared
        this.personalBelongings = belongings != null ? belongings.clone() : new Supply[0];
    }

    public Integer getApproximateAge() {
        return approximateAge;
    }

    // NEW: approximate age and date of birth setters
    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        this.dateOfBirth = dateOfBirth;
        this.approximateAge = null;
    }

    public void setApproximateAge(int approximateAge) {
        if (approximateAge < 0) {
            throw new IllegalArgumentException("Approximate age cannot be negative");
        }
        if (this.dateOfBirth != null) {
            throw new IllegalStateException("Cannot replace birthdate with approximate age");
        }

        this.approximateAge = approximateAge;
    }

    public void addPersonalBelonging(Supply supply) {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }

        if (this.personalBelongings == null) {
            Supply tmpSupply[] = { supply };
            this.setPersonalBelongings(tmpSupply);
            return;
        }

        // Create an array one larger than the previous array
        int newLength = this.personalBelongings.length + 1;
        Supply tmpPersonalBelongings[] = new Supply[newLength];

        // Copy all the items in the current array to the new array
        int i;
        for (i=0; i < personalBelongings.length; i++) {
            tmpPersonalBelongings[i] = this.personalBelongings[i];
        }

        // Add the new element at the end of the new array
        tmpPersonalBelongings[i] = supply;

        // Replace the original array with the new array
        this.personalBelongings = tmpPersonalBelongings;
    }

    public void removePersonalBelonging(Supply unwantedSupply) throws IllegalArgumentException {
        if (unwantedSupply == null) {
            throw new IllegalArgumentException("Supply to remove cannot be null");
        }
        
        // Find the supply - must use equals() for proper comparison
        int index = -1;
        for (int i = 0; i < personalBelongings.length; i++) {
            if (personalBelongings[i].equals(unwantedSupply)) {
                index = i;
                break;
            }
        }
        
        // If not found, throw exception
        if (index == -1) {
            throw new IllegalArgumentException("Supply not found in personal belongings");
        }
        
        // When a personal belonging is removed, it is destroyed (not returned to supply). 
        // We create a new array without the item.
        Supply[] updatedBelongings = new Supply[personalBelongings.length - 1];
        int newIndex = 0;
        for (int i = 0; i < personalBelongings.length; i++) {
            if (i != index) {
                updatedBelongings[newIndex] = personalBelongings[i];
                newIndex++;
            }
        }
        
        this.personalBelongings = updatedBelongings;
    }

    public void removeFamilyConnection(FamilyRelation exRelation) throws IllegalArgumentException {
        if (exRelation == null) {
            throw new IllegalArgumentException("Family relation to remove cannot be null");
        }
        
        int index = -1;
        for (int i = 0; i < familyConnections.length; i++) {
            if (familyConnections[i].equals(exRelation)) {
                index = i;
                break;
            }
        }
        
        if (index == -1) {
            throw new IllegalArgumentException("Family relation not found");
        }
        
        FamilyRelation[] updatedConnections = new FamilyRelation[familyConnections.length - 1];
        int newIndex = 0;
        for (int i = 0; i < familyConnections.length; i++) {
            if (i != index) {
                updatedConnections[newIndex] = familyConnections[i];
                newIndex++;
            }
        }
        
        this.familyConnections = updatedConnections;
    }

    public void addFamilyConnection(FamilyRelation record) {
        if (record == null) {
            throw new IllegalArgumentException("Family relation cannot be null");
        }
        
        FamilyRelation[] newConnections = new FamilyRelation[familyConnections.length + 1];
        System.arraycopy(familyConnections, 0, newConnections, 0, familyConnections.length);
        newConnections[familyConnections.length] = record;
        this.familyConnections = newConnections;
    }

    public void addMedicalRecord(MedicalRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Medical record cannot be null");
        }
        
        MedicalRecord[] newRecords = new MedicalRecord[medicalRecords.length + 1];
        System.arraycopy(medicalRecords, 0, newRecords, 0, medicalRecords.length);
        newRecords[medicalRecords.length] = record;
        this.medicalRecords = newRecords;
    }

    public LocalDate getEntryDate() {
        return ENTRY_DATE;
    }

    public String getComments() {
        return person.getComments();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) throws IllegalArgumentException {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }
        
        String normalizedGender = gender.trim();
        String lowerGender = normalizedGender.toLowerCase();
        
        // If gender is already set to "Please specify", allow any new value
        if (this.gender != null && this.gender.equalsIgnoreCase("please specify")) {
            this.gender = normalizedGender;
            return;
        }
        
        String[] adultOptions = {"man", "woman"};
        String[] childOptions = {"boy", "girl"};
        
        // Check for "please specify" option
        if (lowerGender.equals("please specify")) {
            this.gender = normalizedGender;  // Store "Please specify" as-is
            return;
        }
        
        boolean isValidOption = false;
        String properCaseOption = null;
        
        for (String option : adultOptions) {
            if (lowerGender.equals(option)) {
                isValidOption = true;
                properCaseOption = option.substring(0, 1).toUpperCase() + option.substring(1);
                break;
            }
        }
        
        if (!isValidOption) {
            for (String option : childOptions) {
                if (lowerGender.equals(option)) {
                    isValidOption = true;
                    properCaseOption = option.substring(0, 1).toUpperCase() + option.substring(1);
                    break;
                }
            }
        }
        
        if (!isValidOption) {
            throw new IllegalArgumentException(
                "Invalid gender. Acceptable values are: Man, Woman, Boy, Girl, or 'Please specify'"
            );
        }
        
        if (this.dateOfBirth != null) {
            int age = LocalDate.now().getYear() - this.dateOfBirth.getYear();
            boolean isAdult = age >= 18;
            
            if (isAdult) {
                for (String childOption : childOptions) {
                    if (lowerGender.equals(childOption)) {
                        throw new IllegalArgumentException(
                            "Cannot set gender to '" + properCaseOption + "' for an adult (age " + age + ")"
                        );
                    }
                }
            } else {
                for (String adultOption : adultOptions) {
                    if (lowerGender.equals(adultOption)) {
                        throw new IllegalArgumentException(
                            "Cannot set gender to '" + properCaseOption + "' for a child (age " + age + ")"
                        );
                    }
                }
            }
        }
        
        this.gender = properCaseOption;
    }
}
