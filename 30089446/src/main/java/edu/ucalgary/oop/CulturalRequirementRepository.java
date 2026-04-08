package edu.ucalgary.oop;

import java.sql.*;
import java.util.*;

public class CulturalRequirementRepository {
    private final Connection dbConnect;

    // constructor
    public CulturalRequirementRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    // add requirement for victim
    public void insertRequirement(int victimId, String category, String option) {
        String query = "INSERT INTO CulturalRequirement (victim_id, requirement_category, requirement_option) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.setString(2, category);
            stmt.setString(3, option);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert cultural requirement.", e);
        }
    }

    // update requirement
    public void updateRequirement(int victimId, String category, String newOption) {
        String query = "UPDATE CulturalRequirement SET requirement_option = ? WHERE victim_id = ? AND requirement_category = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, newOption);
            stmt.setInt(2, victimId);
            stmt.setString(3, category);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException(
                    "No cultural requirement found for victim id " + victimId + " and category " + category
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update cultural requirement.", e);
        }
    }

    // delete requirement
    public void deleteRequirement(int victimId, String category) {
        String query = "DELETE FROM CulturalRequirement WHERE victim_id = ? AND requirement_category = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.setString(2, category);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException(
                    "No cultural requirement found for victim id " + victimId + " and category " + category
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete cultural requirement.", e);
        }
    }

    // delete all requirements for a victim
    public void deleteAllRequirementsForVictim(int victimId) {
        String query = "DELETE FROM CulturalRequirement WHERE victim_id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete cultural requirements for victim.", e);
        }
    }

    // getters
    public List<CulturalRequirement> getRequirementsForVictim(int victimId) {
        String query = "SELECT victim_id, requirement_category, requirement_option FROM CulturalRequirement WHERE victim_id = ? ORDER BY requirement_category";

        List<CulturalRequirement> requirements = new ArrayList<>();

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CulturalRequirement requirement = new CulturalRequirement(
                        rs.getInt("victim_id"),
                        rs.getString("requirement_category"),
                        rs.getString("requirement_option")
                    );
                    requirements.add(requirement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get cultural requirements for victim.", e);
        }

        return requirements;
    }

    // check if victim has requirement
    public boolean victimHasRequirement(int victimId, String category) {
        String query = "SELECT 1 FROM CulturalRequirement WHERE victim_id = ? AND requirement_category = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.setString(2, category);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check cultural requirement.", e);
        }
    }
}