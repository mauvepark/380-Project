package edu.ucalgary.oop;

import java.sql.*;
import java.util.*;

public class LocationService {
    private final LocationRepository repository;
    private final ActionLogger logger;

    // constructor
    public LocationService() {
        this.repository = new LocationRepository(DatabaseManager.getInstance().getConnection());
        this.logger = ActionLogger.getInstance();
    }

    // add a new location
    public Location addLocation(String name, String address) {
        validateNameAndAddress(name, address);

        int locationId = repository.insertLocation(name, address);
        Location location = new Location(locationId, name, address);

        logger.log("ADDED", "location " + locationId + " | Name: " + location.getName());

        return location;
    }

    // update location
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

    // delete location
    public void deleteLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }

        repository.deleteLocation(location.getId());

        logger.log("DELETED", "location " + location.getId() + " | Name: " + location.getName());
    }

    // get one location by id
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

    // get all locations
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

    // check if location exists
    public boolean locationExists(int locationId) {
        if (locationId <= 0) {
            throw new IllegalArgumentException("Location ID must be positive.");
        }

        return repository.locationExists(locationId);
    }

    // validation
    private void validateNameAndAddress(String name, String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be null or blank.");
        }

        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Location address cannot be null or blank.");
        }
    }
}