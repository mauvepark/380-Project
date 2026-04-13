package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for the MedicalRecord class. These tests cover constructor validation as well as setter/getter methods for model class.
 */
public class MedicalRecordTest {

    private MedicalRecord record;

    @Before
    public void setUp() {
        record = new MedicalRecord(
            1,
            2,
            "Test record",
            LocalDate.parse("2026-01-01"),
            10
        );
    }

    @Test
    public void testConstructorWithValidData() {
        MedicalRecord medicalRecord = new MedicalRecord(
            1,
            2,
            "Test record",
            LocalDate.parse("2026-01-01"),
            10
        );

        assertNotNull("MedicalRecord should be created", medicalRecord);
        assertEquals("ID should match", 1, medicalRecord.getId());
        assertEquals("Victim ID should match", 2, medicalRecord.getVictimId());
        assertEquals("Treatment details should match", "Test record", medicalRecord.getTreatmentDetails());
        assertEquals("Treatment date should match", LocalDate.parse("2026-01-01"), medicalRecord.getTreatmentDate());
        assertEquals("Location ID should match", Integer.valueOf(10), medicalRecord.getLocationId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidVictimId() {
        new MedicalRecord(
            1,
            0,
            "Test record",
            LocalDate.parse("2026-01-01"),
            10
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBlankTreatmentDetails() {
        new MedicalRecord(
            1,
            2,
            "",
            LocalDate.parse("2026-01-01"),
            10
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullTreatmentDate() {
        new MedicalRecord(
            1,
            2,
            "Test record",
            null,
            10
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFutureTreatmentDate() {
        new MedicalRecord(
            1,
            2,
            "Test record",
            LocalDate.now().plusDays(1),
            10
        );
    }

    @Test
    public void testSetIdValid() {
        record.setId(5);
        assertEquals("ID should update", 5, record.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIdNegative() {
        record.setId(-1);
    }

    @Test
    public void testSetAVictimIdValid() {
        record.setVictimId(7);
        assertEquals("Victim ID should update", 7, record.getVictimId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetVictimIdInvalid() {
        record.setVictimId(0);
    }

    @Test
    public void testSetAndGetTreatmentDetails() {
        record.setTreatmentDetails("Update");
        assertEquals("Treatment details should update", "Update", record.getTreatmentDetails());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTreatmentDetailsBlankInvalid() {
        record.setTreatmentDetails("");
    }

    @Test
    public void testSetAndGetTreatmentDate() {
        record.setTreatmentDate(LocalDate.parse("2026-01-01"));
        assertEquals("Treatment date should update", LocalDate.parse("2026-01-01"), record.getTreatmentDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTreatmentDateNullInvalid() {
        record.setTreatmentDate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTreatmentDateFutureInvalid() {
        record.setTreatmentDate(LocalDate.now().plusDays(1));
    }

    @Test
    public void testSetAndGetLocationId() {
        record.setLocationId(15);
        assertEquals("Location ID should update", Integer.valueOf(15), record.getLocationId());
    }

    @Test
    public void testSetLocationIdNullValid() {
        record.setLocationId(null);
        assertNull("Location ID should become null", record.getLocationId());
    }
}