package edu.ucalgary.oop;

import java.sql.*;

public class LocationRepository {
    private final Connection dbConnect;

    // constructor
    public LocationRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    // add new location
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

    // update location
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

    // delete location
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

    // get location by id
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

    // get all locations
    public ResultSet getAllLocations() {
        String query = "SELECT id, name, address FROM Location ORDER BY id";

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get locations.", e);
        }
    }

    // check if location exists
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