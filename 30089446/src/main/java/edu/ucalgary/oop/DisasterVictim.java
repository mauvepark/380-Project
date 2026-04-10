package edu.ucalgary.oop;

import java.time.LocalDate;

public class DisasterVictim extends Person {
    private LocalDate dateOfBirth;
    private Integer approximateAge;
    private FamilyRelation[] familyConnections;
    private MedicalRecord[] medicalRecords;
    private Supply[] personalBelongings;
    private final LocalDate ENTRY_DATE;
    private String gender;
    private Location location;

    public DisasterVictim(int id, String firstName, String lastName, String comments,
                          LocalDate entryDate, LocalDate dateOfBirth) {
        super(id, firstName, lastName, comments);

        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        this.ENTRY_DATE = entryDate;
        this.dateOfBirth = dateOfBirth;
        this.approximateAge = null;
        this.familyConnections = new FamilyRelation[0];
        this.medicalRecords = new MedicalRecord[0];
        this.personalBelongings = new Supply[0];
    }

    public DisasterVictim(int id, String firstName, String lastName, String comments,
                          LocalDate entryDate, int approximateAge) {
        super(id, firstName, lastName, comments);

        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }
        if (approximateAge < 0) {
            throw new IllegalArgumentException("Approximate age cannot be negative");
        }

        this.ENTRY_DATE = entryDate;
        this.approximateAge = approximateAge;
        this.dateOfBirth = null;
        this.familyConnections = new FamilyRelation[0];
        this.medicalRecords = new MedicalRecord[0];
        this.personalBelongings = new Supply[0];
    }

    public DisasterVictim(String firstName, String lastName, String comments,
                          LocalDate entryDate, LocalDate dateOfBirth) {
        super(firstName, lastName, comments);

        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        this.ENTRY_DATE = entryDate;
        this.dateOfBirth = dateOfBirth;
        this.approximateAge = null;
        this.familyConnections = new FamilyRelation[0];
        this.medicalRecords = new MedicalRecord[0];
        this.personalBelongings = new Supply[0];
    }

    public DisasterVictim(String firstName, String lastName, String comments,
                          LocalDate entryDate, int approximateAge) {
        super(firstName, lastName, comments);

        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null");
        }
        if (approximateAge < 0) {
            throw new IllegalArgumentException("Approximate age cannot be negative");
        }

        this.ENTRY_DATE = entryDate;
        this.approximateAge = approximateAge;
        this.dateOfBirth = null;
        this.familyConnections = new FamilyRelation[0];
        this.medicalRecords = new MedicalRecord[0];
        this.personalBelongings = new Supply[0];
    }

    public int getPersonId() {
        return getId();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getApproximateAge() {
        return approximateAge;
    }

    public FamilyRelation[] getFamilyConnections() {
        return familyConnections;
    }

    public MedicalRecord[] getMedicalRecords() {
        return medicalRecords;
    }

    public Supply[] getPersonalBelongings() {
        return personalBelongings;
    }

    public void setFamilyConnections(FamilyRelation[] connections) {
        this.familyConnections = connections != null ? connections.clone() : new FamilyRelation[0];
    }

    public void setMedicalRecords(MedicalRecord[] records) {
        this.medicalRecords = records != null ? records.clone() : new MedicalRecord[0];
    }

    public void setPersonalBelongings(Supply[] belongings) {
        this.personalBelongings = belongings != null ? belongings.clone() : new Supply[0];
    }

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void addPersonalBelonging(Supply supply) {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }

        Supply[] updated = new Supply[personalBelongings.length + 1];
        System.arraycopy(personalBelongings, 0, updated, 0, personalBelongings.length);
        updated[personalBelongings.length] = supply;
        this.personalBelongings = updated;
    }

    public void removePersonalBelonging(Supply unwantedSupply) {
        if (unwantedSupply == null) {
            throw new IllegalArgumentException("Supply to remove cannot be null");
        }

        int index = -1;
        for (int i = 0; i < personalBelongings.length; i++) {
            if (personalBelongings[i].equals(unwantedSupply)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Supply not found in personal belongings");
        }

        Supply[] updated = new Supply[personalBelongings.length - 1];
        int newIndex = 0;
        for (int i = 0; i < personalBelongings.length; i++) {
            if (i != index) {
                updated[newIndex] = personalBelongings[i];
                newIndex++;
            }
        }

        this.personalBelongings = updated;
    }

    public void removeFamilyConnection(FamilyRelation relation) {
        if (relation == null) {
            throw new IllegalArgumentException("Family relation to remove cannot be null");
        }

        removeFamilyConnectionInternal(relation);

        DisasterVictim other;
        if (relation.getPersonOne() == this) {
            other = (DisasterVictim) relation.getPersonTwo();
        } else if (relation.getPersonTwo() == this) {
            other = (DisasterVictim) relation.getPersonOne();
        } else {
            throw new IllegalArgumentException("This victim is not part of the family relation");
        }

        if (other != null) {
            other.removeFamilyConnectionInternal(relation);
        }
    }

    private void removeFamilyConnectionInternal(FamilyRelation relation) {
        int index = -1;
        for (int i = 0; i < familyConnections.length; i++) {
            if (familyConnections[i].equals(relation)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return;
        }

        FamilyRelation[] updated = new FamilyRelation[familyConnections.length - 1];
        int newIndex = 0;
        for (int i = 0; i < familyConnections.length; i++) {
            if (i != index) {
                updated[newIndex] = familyConnections[i];
                newIndex++;
            }
        }

        this.familyConnections = updated;
    }

    public void addFamilyConnection(FamilyRelation relation) {
        if (relation == null) {
            throw new IllegalArgumentException("Family relation cannot be null");
        }

        addFamilyConnectionInternal(relation);

        DisasterVictim other;
        if (relation.getPersonOne() == this) {
            other = (DisasterVictim) relation.getPersonTwo();
        } else if (relation.getPersonTwo() == this) {
            other = (DisasterVictim) relation.getPersonOne();
        } else {
            throw new IllegalArgumentException("This victim is not part of the family relation");
        }

        if (other != null) {
            other.addFamilyConnectionInternal(relation);
        }
    }

    private void addFamilyConnectionInternal(FamilyRelation relation) {
        for (FamilyRelation existing : familyConnections) {
            if (existing.equals(relation)) {
                return;
            }
        }

        FamilyRelation[] updated = new FamilyRelation[familyConnections.length + 1];
        System.arraycopy(familyConnections, 0, updated, 0, familyConnections.length);
        updated[familyConnections.length] = relation;
        this.familyConnections = updated;
    }

    public void addMedicalRecord(MedicalRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Medical record cannot be null");
        }

        MedicalRecord[] updated = new MedicalRecord[medicalRecords.length + 1];
        System.arraycopy(medicalRecords, 0, updated, 0, medicalRecords.length);
        updated[medicalRecords.length] = record;
        this.medicalRecords = updated;
    }

    public LocalDate getEntryDate() {
        return ENTRY_DATE;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }

        String normalizedGender = gender.trim();
        String lowerGender = normalizedGender.toLowerCase();

        if (this.gender != null && this.gender.equalsIgnoreCase("please specify")) {
            this.gender = normalizedGender;
            return;
        }

        String[] adultOptions = {"man", "woman"};
        String[] childOptions = {"boy", "girl"};

        if (lowerGender.equals("please specify")) {
            this.gender = normalizedGender;
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