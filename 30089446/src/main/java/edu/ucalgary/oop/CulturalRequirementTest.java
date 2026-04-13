package edu.ucalgary.oop;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the CulturalRequirement class.
 * These tests check object creation and validation for cultural requirements.
 */
public class CulturalRequirementTest {


    @Test
    public void testWithValidData() {
        CulturalRequirement requirement = new CulturalRequirement(5, "dietary restrictions", "halal");

        assertNotNull("Constructor should create a CulturalRequirement object", requirement);
        assertEquals("Victim ID should be set correctly", 5, requirement.getVictimId());
        assertEquals(
            "Category should be set correctly",
            "dietary restrictions",
            requirement.getCategory()
        );
        assertEquals("Option should be set correctly", "halal", requirement.getOption());
    }

    @Test
    public void testObjectCreated() {
        CulturalRequirement requirement =
            new CulturalRequirement(3, "language support", "translation services");

        assertNotNull("CulturalRequirement object should be created", requirement);
    }

    @Test
    public void testSetVictimId() {
        CulturalRequirement requirement = new CulturalRequirement(1, "dietary restrictions", "vegetarian");

        requirement.setVictimId(7);

        assertEquals("setVictimId should update victim ID", 7, requirement.getVictimId());
    }

    @Test
    public void testSetCategory() {
        CulturalRequirement requirement =
            new CulturalRequirement(1, "dietary restrictions", "vegetarian");

        requirement.setCategory("safe-space requirements");

        assertEquals(
            "setCategory should update category",
            "safe-space requirements",
            requirement.getCategory()
        );
    }

    @Test
    public void testSetOption() {
        CulturalRequirement requirement =
            new CulturalRequirement(1, "dietary restrictions", "vegetarian");

        requirement.setOption("halal");

        assertEquals("setOption should update option", "halal", requirement.getOption());
    }
}
