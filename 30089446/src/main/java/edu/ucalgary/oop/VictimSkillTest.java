package edu.ucalgary.oop;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for the VictimSkill class.
 * These tests check object creation and validation for victim skill records.
 */
public class VictimSkillTest {

    @Test
    public void testConstructorWithValidData() {
        VictimSkill victimSkill = new VictimSkill(
            1,
            2,
            3,
            "drivers license",
            null,
            LocalDate.parse("2026-01-01"),
            "advanced"
        );

        assertNotNull("VictimSkill should be created", victimSkill);
        assertEquals("ID should match", 1, victimSkill.getId());
        assertEquals("Victim ID should match", 2, victimSkill.getVictimId());
        assertEquals("Skill ID should match", 3, victimSkill.getSkillId());
        assertEquals("Details should match", "drivers license", victimSkill.getDetails());
        assertEquals("Proficiency should match", "advanced", victimSkill.getProficiencyLevel());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidVictimId() {
        new VictimSkill(
            1,
            0,
            3,
            null,
            null,
            null,
            "advanced"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidSkillId() {
        new VictimSkill(
            1,
            2,
            0,
            null,
            null,
            null,
            "advanced"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBlankProficiencyInvalid() {
        new VictimSkill(
            1,
            2,
            3,
            null,
            null,
            null,
            ""
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidProficiencyInvalid() {
        new VictimSkill(
            1,
            2,
            3,
            null,
            null,
            null,
            "suuuper good"
        );
    }

    @Test
    public void testSetDetailsNullValid() {
        VictimSkill victimSkill = new VictimSkill(
            1,
            2,
            3,
            "drivers license",
            null,
            null,
            "beginner"
        );

        victimSkill.setDetails(null);

        assertNull("Details should become null", victimSkill.getDetails());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDetailsBlankInvalid() {
        VictimSkill victimSkill = new VictimSkill(
            1,
            2,
            3,
            "drivers license",
            null,
            null,
            "advanced"
        );

        victimSkill.setDetails("");
    }

    @Test
    public void testSetLanguageCapabilitiesNullValid() {
        VictimSkill victimSkill = new VictimSkill(
            1,
            2,
            3,
            null,
            "read/write",
            null,
            "advanced"
        );

        victimSkill.setLanguageCapabilities(null);

        assertNull("Language capabilities should become null", victimSkill.getLanguageCapabilities());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLanguageCapabilitiesBlankInvalid() {
        VictimSkill victimSkill = new VictimSkill(
            1,
            2,
            3,
            null,
            "read/write",
            null,
            "advanced"
        );

        victimSkill.setLanguageCapabilities("");
    }

    @Test
    public void testSetCertificationExpiry() {
        VictimSkill victimSkill = new VictimSkill(
            1,
            2,
            3,
            null,
            null,
            null,
            "advanced"
        );

        victimSkill.setCertificationExpiry(LocalDate.parse("2030-01-01"));

        assertEquals("Certification expiry should update", LocalDate.parse("2030-01-01"), victimSkill.getCertificationExpiry());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIdNegativeInvalid() {
        VictimSkill victimSkill = new VictimSkill(
            1,
            2,
            3,
            null,
            null,
            null,
            "advanced"
        );

        victimSkill.setId(-1);
    }
}
