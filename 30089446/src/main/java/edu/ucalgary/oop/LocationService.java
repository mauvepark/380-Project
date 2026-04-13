package edu.ucalgary.oop;

import java.sql.*;
import java.util.*;

/**
 * Service class for managing Location entities. 
 * Provides methods to add, update, delete, and retrieve locations, as well as check for their existence.
 */
public class LocationService {
    private final LocationRepository repository;
    private final ActionLogger logger;

    /**
     * Constructor for LocationService. 
     * Initializes the LocationRepository with a database connection and the ActionLogger instance.
     */
    public LocationService() {
        this.repository = new LocationRepository(DatabaseManager.getInstance().getConnection());
        this.logger = ActionLogger.getInstance();
    }

    /**
     * Constructor for LocationService that allows for dependency injection of the LocationRepository and ActionLogger.
     * This constructor can be used for testing purposes to provide mock implementations of the repository and logger.
     * 
     * @param repository the LocationRepository to use for database operations
     * @param logger the ActionLogger to use for logging actions performed by this service
     */
    public LocationService(LocationRepository repository, ActionLogger logger) {
        this.repository = repository;
        this.logger = logger;
    }

    /**
     * Adds a new location with the specified name and address, and returns the created Location object.
     * 
     * @param name the name of the new location (cannot be null or blank)
     * @param address the address of the new location (cannot be null or blank)
     * @return the created Location object with its assigned ID
     */
    public Location addLocation(String name, String address) {
        validateNameAndAddress(name, address);

        int locationId = repository.insertLocation(name, address);
        Location location = new Location(locationId, name, address);

        logger.log("ADDED", "location " + locationId + " | Name: " + location.getName());

        return location;
    }

    /**
     * Updates the name and address of an existing location.
     * 
     * @param location the location to update
     * @param newName the new name for the location
     * @param newAddress the new address for the location
     * @throws IllegalArgumentException if the location is null or if the new name or address are invalid
     */
    public void updateLocation(Location location, String newName, String newAddress) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }

        validateNameAndAddress(newName, newAddress);

        String oldName = location.getName();
        String oldAddress = location.getAddress();

        repository.updateLocation(location.getId(), newName, newAddress);

        location.setName(newName);
        location.setAddress(newAddress);

        logger.log("UPDATED", "location " + location.getId() + " | Name: " + oldName + " -> " 
                    + newName + ", Address: " + oldAddress + " -> " + newAddress);
    }

    /**
     * Deletes an existing location from the database.
     * 
     * @param location the location to deleted
     * @throws IllegalArgumentException if the location is null.
     * 
     */
    public void deleteLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }

        repository.deleteLocation(location.getId());

        logger.log("DELETED", "location " + location.getId() + " | Name: " + location.getName());
    }

    /**
     * Retrieves a location by its ID. Returns null if no location with the given ID exists.
     * 
     * @param locationId the ID of the location to retrieve (must be positive)
     * @return the Location object with the specified ID, or null if not found
     * @throws IllegalArgumentException if the locationId is not positive
     * @throws RuntimeException if there is an error retrieving the location from the database.
     */
    public Location getLocationById(int locationId) {
        if (locationId <= 0) {
            throw new IllegalArgumentException("Location ID must be positive.");
        }

        try (ResultSet rs = repository.getLocationById(locationId)) {
            if (rs.next()) {
                return new Location(rs.getInt("id"), rs.getString("name"), rs.getString("address"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load location.", e);
        }

        return null;
    }

    /**
     * Retrieves all locations from the database, ordered by their ID.
     * 
     * @return a list of all Location objects in the database, ordered by their ID
     * @throws RuntimeException if there is an error retrieving the locations from the database.
     */
    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();

        try (ResultSet rs = repository.getAllLocations()) {
            while (rs.next()) {
                Location location = new Location(rs.getInt("id"), rs.getString("name"), rs.getString("address"));
                locations.add(location);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load locations.", e);
        }

        return locations;
    }

    /**
     * Checks if a location with the given ID exists in the database.
     * 
     * @param locationId the ID of the location to check for existence (must be positive)
     * @return true if a location with the given ID exists, false otherwise
     * @throws IllegalArgumentException if the locationId is not positive
     */
    public boolean locationExists(int locationId) {
        if (locationId <= 0) {
            throw new IllegalArgumentException("Location ID must be positive.");
        }

        return repository.locationExists(locationId);
    }

    /**
     * Validates the name and address for a location. Both name and address must be non-null and non-blank.
     * 
     * @param name the name of the location to validate
     * @param address the address of the location to validate
     * @throws IllegalArgumentException if the name or address are null or blank
     */
    private void validateNameAndAddress(String name, String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be null or blank.");
        }

        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Location address cannot be null or blank.");
        }
    }
}