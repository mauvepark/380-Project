package edu.ucalgary.oop;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Unit tests for the Supply class.
 * These tests check object creation, validation, and supply status methods.
 */
public class SupplyTest {

    @Test
    public void testConstructorWithValidData() {
        Supply supply = new Supply(
            1,
            "blanket",
            2,
            null,
            null,
            null,
            null
        );

        assertNotNull("Supply should be created", supply);
        assertEquals("ID should match", 1, supply.getId());
        assertEquals("Supply type should match", "blanket", supply.getSupplyType());
        assertEquals("Location ID should match", Integer.valueOf(2), supply.getLocationId());
        assertNull("Victim ID should be null", supply.getVictimId());
        assertNull("Description should be null", supply.getDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBlankSupplyTypeInvalid() {
        new Supply(
            1,
            "",
            2,
            null,
            null,
            null,
            null
        );
    }

    @Test
    public void testSetSupplyType() {
        Supply supply = new Supply("gauze", 1, null, null, null, null);
        supply.setSupplyType("bandage");

        assertEquals("Supply type should update", "bandage", supply.getSupplyType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSupplyTypeBlankInvalid() {
        Supply supply = new Supply("water", 1, null, null, null, null);
        supply.setSupplyType("");
    }

    @Test
    public void testSetLocationId() {
        Supply supply = new Supply("blanket", 1, null, null, null, null);
        supply.setLocationId(5);

        assertEquals("Location ID should update", Integer.valueOf(5), supply.getLocationId());
    }

    @Test
    public void testSetVictimId() {
        Supply supply = new Supply("blanket", 1, null, null, null, null);
        supply.setVictimId(7);

        assertEquals("Victim ID should update", Integer.valueOf(7), supply.getVictimId());
    }

    @Test
    public void testSetExpiryDate() {
        Supply supply = new Supply("food", 1, null, null, null, null);
        LocalDate expiry = LocalDate.parse("2026-01-01");
        supply.setExpiryDate(expiry);

        assertEquals("Expiry date should update", expiry, supply.getExpiryDate());
    }

    @Test
    public void testSetAllocationDate() {
        Supply supply = new Supply("blanket", 1, null, null, null, null);
        LocalDate allocationDate = LocalDate.parse("2025-01-18");
        supply.setAllocationDate(allocationDate);

        assertEquals("Allocation date should update", allocationDate, supply.getAllocationDate());
    }

    @Test
    public void testIsPerishableTrue() {
        Supply supply = new Supply(
            "food",
            1,
            null,
            LocalDate.parse("2026-01-01"),
            null,
            null
        );

        assertTrue("Supply with expiry date should be perishable", supply.isPerishable());
    }

    @Test
    public void testIsPerishableFalse() {
        Supply supply = new Supply(
            "blanket",
            1,
            null,
            null,
            null,
            null
        );

        assertFalse("Supply without expiry date should not be perishable", supply.isPerishable());
    }

    @Test
    public void testIsExpiredTrue() {
        Supply supply = new Supply(
            "food",
            1,
            null,
            LocalDate.now().minusDays(1),
            null,
            null
        );

        assertTrue("Supply past expiry date should be labelled expired", supply.isExpired());
    }

    @Test
    public void testIsExpiredFalse() {
        Supply supply = new Supply(
            "food",
            1,
            null,
            LocalDate.now().plusDays(1),
            null,
            null
        );

        assertFalse("Supply with future expiry date should not be labelled expired", supply.isExpired());
    }

    @Test
    public void testIsAllocatedTrue() {
        Supply supply = new Supply(
            "water",
            1,
            10,
            null,
            LocalDate.parse("2026-01-01"),
            null
        );

        assertTrue("Supply with victim ID should be allocated", supply.isAllocated());
    }

    @Test
    public void testIsAllocatedFalse() {
        Supply supply = new Supply(
            "water",
            1,
            null,
            null,
            null,
            null
        );

        assertFalse("Supply without victim ID should not be allocated", supply.isAllocated());
    }
}
