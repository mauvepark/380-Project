package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the LocationService class.
 * These tests check adding, updating, deleting, and retrieving locations.
 */
public class LocationServiceTest {

    private LocationService service;
    private FakeLocationRepository fakeRepository;
    private FakeActionLogger fakeLogger;

    @Before
    public void setUp() {
        fakeRepository = new FakeLocationRepository();
        fakeLogger = new FakeActionLogger();
        service = new LocationService(fakeRepository, fakeLogger);
    }

    @Test
    public void testAddLocationValid() {
        Location location = service.addLocation("A", "123 Hi St");

        assertNotNull("Location should be created", location);
        assertEquals("Location ID should be assigned", 1, location.getId());
        assertEquals("Location name should match", "A", location.getName());
        assertEquals("Location address should match", "123 Hi St", location.getAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLocationNullNameInvalid() {
        service.addLocation(null, "123 Hi St");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLocationBlankNameInvalid() {
        service.addLocation("   ", "123 Hi St");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLocationNullAddressInvalid() {
        service.addLocation("A", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLocationBlankAddressInvalid() {
        service.addLocation("A", "   ");
    }

    @Test
    public void testUpdateLocationValid() {
        Location location = service.addLocation("Old location", "Old Address");

        service.updateLocation(location, "New location", "New Address");

        assertEquals("Location name should be updated", "New location", location.getName());
        assertEquals("Location address should be updated", "New Address", location.getAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateLocationNullLocationInvalid() {
        service.updateLocation(null, "New location", "New Address");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateLocationBlankNameInvalid() {
        Location location = service.addLocation("Old location", "Old Address");
        service.updateLocation(location, "   ", "New Address");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateLocationBlankAddressInvalid() {
        Location location = service.addLocation("Old location", "Old Address");
        service.updateLocation(location, "New location", "   ");
    }

    @Test
    public void testDeleteLocationValid() {
        Location location = service.addLocation("A", "123 Hi St");

        assertTrue("Location should exist before delete", service.locationExists(location.getId()));

        service.deleteLocation(location);

        assertFalse("Location should not exist after delete", service.locationExists(location.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteLocationNullInvalid() {
        service.deleteLocation(null);
    }

    @Test
    public void testLocationExistsTrue() {
        Location location = service.addLocation("A", "123 Hi St");

        assertTrue("locationExists should return true for saved location", service.locationExists(location.getId()));
    }

    @Test
    public void testLocationExistsFalse() {
        assertFalse("locationExists should return false for unknown location", service.locationExists(999));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationExistsInvalidId() {
        service.locationExists(0);
    }

    private static class FakeLocationRepository extends LocationRepository {
        private final Map<Integer, Location> storedLocations = new HashMap<>();
        private int nextId = 1;

        public FakeLocationRepository() {
            super(null);
        }

        @Override
        public int insertLocation(String name, String address) {
            int id = nextId++;
            storedLocations.put(id, new Location(id, name, address));
            return id;
        }

        @Override
        public void updateLocation(int locationId, String newName, String newAddress) {
            Location location = storedLocations.get(locationId);

            if (location == null) {
                throw new RuntimeException("No location found with id " + locationId);
            }

            location.setName(newName);
            location.setAddress(newAddress);
        }

        @Override
        public void deleteLocation(int locationId) {
            if (storedLocations.remove(locationId) == null) {
                throw new RuntimeException("No location found with id " + locationId);
            }
        }

        @Override
        public boolean locationExists(int locationId) {
            return storedLocations.containsKey(locationId);
        }
    }

    private static class FakeActionLogger extends ActionLogger {
        @Override
        public void log(String actionType, String description) {
            // do nothing for tests
        }
    }
}
