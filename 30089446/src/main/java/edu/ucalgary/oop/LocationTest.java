package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class LocationTest {

    private Location location;
    private DisasterVictim victim;
    private Supply supply;

    @Before
    public void setUp() {
        location = new Location(1, "Hi", "123 Hi St");
        victim = new DisasterVictim(
            1,
            "Noor",
            "Ali",
            null,
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2000-01-01")
        );
        supply = new Supply(
            1,
            "gauze",
            1,
            null,
            null,
            null,
            null
        );
    }

    @Test
    public void testConstructorWithIdValid() {
        Location testLocation = new Location(5, "Bye", "123 Bye St");

        assertNotNull("Location should be created", testLocation);
        assertEquals("ID should match", 5, testLocation.getId());
        assertEquals("Name should match", "Bye", testLocation.getName());
        assertEquals("Address should match", "123 Bye St", testLocation.getAddress());
    }

    @Test
    public void testConstructorWithoutIdValid() {
        Location testLocation = new Location("What", "123 What St");

        assertNotNull("Location should be created", testLocation);
        assertEquals("Name should match", "What", testLocation.getName());
        assertEquals("Address should match", "123 What St", testLocation.getAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativeIdInvalid() {
        new Location(-1, "Hi", "123 Hi St");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullNameInvalid() {
        new Location(1, null, "123 Hi St");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBlankNameInvalid() {
        new Location(1, "", "123 Hi St");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullAddressInvalid() {
        new Location(1, "Hi", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBlankAddressInvalid() {
        new Location(1, "Hi", "   ");
    }

    @Test
    public void testSetAndGetId() {
        location.setId(10);
        assertEquals("ID should update", 10, location.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIdNegativeInvalid() {
        location.setId(-1);
    }

    @Test
    public void testSetAndGetName() {
        location.setName("Update");
        assertEquals("Name should update", "Update", location.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNameNullInvalid() {
        location.setName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNameBlankInvalid() {
        location.setName("");
    }

    @Test
    public void testSetAndGetAddress() {
        location.setAddress("123 New St");
        assertEquals("Address should update", "123 New St", location.getAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAddressNullInvalid() {
        location.setAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAddressBlankInvalid() {
        location.setAddress("");
    }

    @Test
    public void testAddOccupantValid() {
        location.addOccupant(victim);

        assertEquals("Location should have one occupant", 1, location.getOccupants().length);
        assertEquals("Added occupant should be stored", victim, location.getOccupants()[0]);
        assertEquals("Victim location should be updated", location, victim.getLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOccupantNullInvalid() {
        location.addOccupant(null);
    }

    @Test
    public void testRemoveOccupantValid() {
        location.addOccupant(victim);
        location.removeOccupant(victim);

        assertEquals("Location should have no occupants after removal", 0, location.getOccupants().length);
        assertNull("Victim location should be cleared after removal", victim.getLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveOccupantNullInvalid() {
        location.removeOccupant(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveOccupantNotFoundInvalid() {
        location.removeOccupant(victim);
    }

    @Test
    public void testSetOccupantsValid() {
        DisasterVictim[] victims = {victim};
        location.setOccupants(victims);

        assertEquals("Occupants array should have one victim", 1, location.getOccupants().length);
        assertEquals("Stored occupant should match", victim, location.getOccupants()[0]);
    }

    @Test
    public void testAddSupplyValid() {
        location.addSupply(supply);

        assertEquals("Location should have one supply", 1, location.getSupplies().length);
        assertEquals("Added supply should be stored", supply, location.getSupplies()[0]);
        assertEquals("Supply location should be updated", location, supply.getLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSupplyNullInvalid() {
        location.addSupply(null);
    }

    @Test
    public void testRemoveSupplyValid() {
        location.addSupply(supply);
        location.removeSupply(supply);

        assertEquals("Location should have no supplies after removal", 0, location.getSupplies().length);
        assertNull("Supply location should be cleared after removal", supply.getLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveSupplyNullInvalid() {
        location.removeSupply(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveSupplyNotFoundInvalid() {
        location.removeSupply(supply);
    }

    @Test
    public void testSetSuppliesNullBecomesEmptyArray() {
        location.setSupplies(null);
        assertEquals("Null supplies should become empty array", 0, location.getSupplies().length);
    }

    @Test
    public void testSetSuppliesValid() {
        Supply[] supplies = {supply};
        location.setSupplies(supplies);

        assertEquals("Supplies array should have one supply", 1, location.getSupplies().length);
        assertEquals("Stored supply should match", supply, location.getSupplies()[0]);
    }
}