package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for the DisasterVictim class.
 * These tests check object creation, updates, and validation for victims.
 */
public class DisasterVictimTest {

    private DisasterVictim victim1;
    private DisasterVictim childVictim;
    private DisasterVictim approximateAgeVictim;

    @Before
    public void setUp() {
        victim1 = new DisasterVictim(
            1,
            "Jane",
            "Doe",
            null,
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("1990-01-01")
        );

        childVictim = new DisasterVictim(
            2,
            "Apple",
            "Bees",
            null,
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2020-01-01")
        );

        approximateAgeVictim = new DisasterVictim(
            3,
            "John",
            "Smith",
            "Approximate age",
            LocalDate.parse("2026-01-01"),
            30
        );
    }

    @Test
    public void testConstructorWithValidBirthdateData() {
        DisasterVictim victim = new DisasterVictim(
            4,
            "Taco",
            "Bell",
            "Valid victim",
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("1995-01-01")
        );

        assertNotNull("Constructor should create a victim object", victim);
        assertEquals("First name should be set correctly", "Taco", victim.getFirstName());
        assertEquals("Last name should be set correctly", "Bell", victim.getLastName());
        assertEquals("Entry date should be set correctly", LocalDate.parse("2026-01-01"), victim.getEntryDate());
        assertEquals("Date of birth should be set correctly", LocalDate.parse("1995-01-01"), victim.getDateOfBirth());
        assertNull("Approximate age should be null when date of birth is used", victim.getApproximateAge());
    }

    @Test
    public void testConstructorWithValidApproximateAgeData() {

        assertNotNull("Constructor should create a victim object", approximateAgeVictim);
        assertEquals("Approximate age should be set correctly", Integer.valueOf(30), approximateAgeVictim.getApproximateAge());
        assertNull("Date of birth should be null when approximate age is used", approximateAgeVictim.getDateOfBirth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullEntryDate() {
        new DisasterVictim(
            5,
            "Jane",
            "Doe",
            "Invalid victim",
            null,
            LocalDate.parse("2026-01-01")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullDOB() {
        new DisasterVictim(
            13,
            "Jane",
            "Doe",
            "Invalid victim",
            LocalDate.parse("2026-01-01"),
            (LocalDate) null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithFutureDOB() {
        new DisasterVictim(
            14,
            "Jane",
            "Doe",
            "Invalid victim",
            LocalDate.parse("2030-01-01"),
            LocalDate.now().plusDays(1)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeApproximateAge() {
        new DisasterVictim(
            15,
            "Negative",
            "Age",
            "Invalid victim",
            LocalDate.parse("2025-01-18"),
            -1
        );
    }

    @Test
    public void testGetPersonId() {
        assertEquals("getPersonId should return the same id as the person id", 1, victim1.getPersonId());
    }

    @Test
    public void testSetDateOfBirthValid() {
        LocalDate newBirthdate = LocalDate.parse("1988-05-21");
        victim1.setDateOfBirth(newBirthdate);

        assertEquals("setDateOfBirth should update the birthdate", newBirthdate, victim1.getDateOfBirth());
        assertNull("Approximate age should become null after setting date of birth", victim1.getApproximateAge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDateOfBirthNull() {
        victim1.setDateOfBirth(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDateOfBirthFutureDate() {
        victim1.setDateOfBirth(LocalDate.now().plusYears(1));
    }

    @Test
    public void testSetApproximateAgeValid() {
        approximateAgeVictim.setApproximateAge(35);
        assertEquals("setApproximateAge should update approximate age", Integer.valueOf(35), approximateAgeVictim.getApproximateAge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetApproximateAgeNegative() {
        approximateAgeVictim.setApproximateAge(-5);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetApproximateAgeWhenBirthdateAlreadyExists() {
        victim1.setApproximateAge(20);
    }

    @Test
    public void testSetGenderManForAdult() {
        victim1.setGender("Man");
        assertEquals("Adult victim should accept Man", "Man", victim1.getGender());
    }

    @Test
    public void testSetGenderWomanForAdultLowercaseInput() {
        victim1.setGender("woman");
        assertEquals("Gender input should be case-insensitive and normalized", "Woman", victim1.getGender());
    }

    @Test
    public void testSetGenderBoyForChild() {
        childVictim.setGender("Boy");
        assertEquals("Child victim should accept Boy", "Boy", childVictim.getGender());
    }

    @Test
    public void testSetGenderGirlForChildUppercaseInput() {
        childVictim.setGender("GIRL");
        assertEquals("Gender input should be normalized to Girl", "Girl", childVictim.getGender());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetGenderBoyForAdultInvalid() {
        victim1.setGender("Boy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetGenderGirlForAdultInvalid() {
        victim1.setGender("Girl");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetGenderManForChildInvalid() {
        childVictim.setGender("Man");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetGenderWomanForChildInvalid() {
        childVictim.setGender("Woman");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetGenderNull() {
        victim1.setGender(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetGenderBlank() {
        victim1.setGender("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetGenderInvalidOption() {
        victim1.setGender("Alien");
    }

    @Test
    public void testSetGenderPleaseSpecifyThenCustomValue() {
        victim1.setGender("Please specify");
        victim1.setGender("Non-binary");

        assertEquals("Please specify should allow any custom gender string afterward", "Non-binary", victim1.getGender());
    }

    @Test
    public void testSetGenderPleaseSpecifyCaseInsensitive() {
        victim1.setGender("please specify");
        victim1.setGender("Genderqueer");

        assertEquals("Please specify should work regardless of case", "Genderqueer", victim1.getGender());
    }

    @Test
    public void testLoadGenderFromDBWithStandardValue() {
        victim1.loadGenderFromDB("woman");
        assertEquals("loadGenderFromDB should print standard values", "Woman", victim1.getGender());
    }

    @Test
    public void testLoadGenderFromDBWithCustomValue() {
        victim1.loadGenderFromDB("non-binary");
        assertEquals("loadGenderFromDB should accept custom free-form values", "non-binary", victim1.getGender());
    }

    @Test
    public void testLoadGenderFromDBWithBlankValue() {
        victim1.loadGenderFromDB("   ");
        assertNull("Blank DB gender should result in null gender", victim1.getGender());
    }

    @Test
    public void testAddPersonalBelonging() {
        Supply supply = new Supply(
            1,
            "gauze",
            2,
            null,
            null,
            null,
            null
        );

        victim1.addPersonalBelonging(supply);

        Supply[] belongings = victim1.getPersonalBelongings();
        assertEquals("Victim should have one personal belonging after add", 1, belongings.length);
        assertEquals("Added supply should be present in belongings", supply, belongings[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPersonalBelongingNull() {
        victim1.addPersonalBelonging(null);
    }

    @Test
    public void testRemovePersonalBelonging() {
        Supply supply = new Supply(
            2,
            "water",
            1,
            null,
            null,
            null,
            null
        );

        victim1.addPersonalBelonging(supply);
        victim1.removePersonalBelonging(supply);

        assertEquals("Victim should have no belongings after removal", 0, victim1.getPersonalBelongings().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemovePersonalBelongingNotFound() {
        Supply supply = new Supply(
            3,
            "food ration",
            1,
            null,
            null,
            null,
            "Not added"
        );

        victim1.removePersonalBelonging(supply);
    }

    @Test
    public void testAddFamilyConnection() {
        DisasterVictim victim2 = new DisasterVictim(
            4,
            "John",
            "Smith",
            null,
            LocalDate.parse("2026-01-01"),
            40
        );

        FamilyRelation relation = new FamilyRelation(1, victim1, "spouse", victim2);

        victim1.addFamilyConnection(relation);

        assertEquals("Victim should have one family connection", 1, victim1.getFamilyConnections().length);
        assertEquals("Other victim should also receive the family connection", 1, victim2.getFamilyConnections().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFamilyConnectionNull() {
        victim1.addFamilyConnection(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAddMedicalRecordNull() {
        victim1.addMedicalRecord(null);
    }

    @Test
    public void testAddMedicalRecord() {
        MedicalRecord record = new MedicalRecord(
            1,
            victim1.getPersonId(),
            "Broken arm untreated",
            LocalDate.parse("2026-01-01"),
            null
        );

        victim1.addMedicalRecord(record);

        assertEquals("Victim should have one medical record after add", 1, victim1.getMedicalRecords().length);
        assertEquals("Medical record should reference the victim after add", victim1, record.getVictim());
    }
}
