package edu.ucalgary.oop;

import java.sql.*;

/**
 * Repository class for managing Location entities in the database. 
 * Provides methods to insert, update, delete, and retrieve locations.
 */
public class LocationRepository {
    private final Connection dbConnect;

    /**
     * Constructor for LocationRepository. Initializes the database connection.
     * 
     * @param dbConnect the Connection object to interact with the database
     */
    public LocationRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    /**
     * Inserts a new location into the database with the given name and address, and returns the generated ID of the new location.
     * 
     * @param name the name of the new location
     * @param address the address of the new location
     * @return the ID of the newly inserted location
     * @throws RuntimeException if there is an error inserting the location into the database.
     */
    public int insertLocation(String name, String address) {
        String query = "INSERT INTO Location (name, address) VALUES (?, ?) RETURNING id";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, address);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert location.", e);
        }

        throw new RuntimeException("Could not insert location.");
    }

    /**
     * Updates an existing location in the database with the given ID, setting its name and address to the new values provided.
     * 
     * @param locationId the ID of the location to update
     * @param newName the new name for the location
     * @param newAddress the new address for the location
     * @throws RuntimeException if there is an error updating the location in the database.
     */
    public void updateLocation(int locationId, String newName, String newAddress) {
        String query = "UPDATE Location SET name = ?, address = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setString(2, newAddress);
            stmt.setInt(3, locationId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No location found with id " + locationId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update location.", e);
        }
    }

    /**
     * Deletes a location from the database with the given ID.
     * 
     * @param locationId the ID of the location to delete
     * @throws RuntimeException if there is an error deleting the location from the database.
     */
    public void deleteLocation(int locationId) {
        String query = "DELETE FROM Location WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, locationId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No location found with id " + locationId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete location.", e);
        }
    }

    /**
     * Retrieves a location from the database by its ID.
     * 
     * @param locationId the ID of the location to retrieve
     * @return a ResultSet containing the location's details (id, name, address) if found, or an empty ResultSet if not found
     * @throws RuntimeException if there is an error retrieving the location from the database.
     */
    public ResultSet getLocationById(int locationId) {
        String query = "SELECT id, name, address FROM Location WHERE id = ?";

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            stmt.setInt(1, locationId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get location.", e);
        }
    }

    /**
     * Retrieves all locations from the database, ordered by their ID.
     * 
     * @return a ResultSet containing the details of all locations (id, name, address) ordered by ID
     * @throws RuntimeException if there is an error retrieving the locations from the database.
     */
    public ResultSet getAllLocations() {
        String query = "SELECT id, name, address FROM Location ORDER BY id";

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get locations.", e);
        }
    }

    /**
     * Checks if a location with the given ID exists in the database.
     * 
     * @param locationId the ID of the location to check for existence
     * @return true if a location with the given ID exists, false otherwise
     * @throws RuntimeException if there is an error checking the location's existence in the database.
     */
    public boolean locationExists(int locationId) {
        String query = "SELECT 1 FROM Location WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, locationId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check location.", e);
        }
    }
}