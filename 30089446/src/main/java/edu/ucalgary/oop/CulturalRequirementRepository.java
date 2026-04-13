package edu.ucalgary.oop;

import java.sql.*;
import java.util.*;

/**
 * Repository class to manage the cultural and religious requirements of disaster victims in the database.
 */
public class CulturalRequirementRepository {

  private final Connection dbConnect;

  // constructor
  public CulturalRequirementRepository(Connection dbConnect) {
    this.dbConnect = dbConnect;
  }

  /**
   * Inserts a new cultural requirement for a victim into the database.
   * 
   * @param victimId The ID of the victim for whom the requirement is being added.
   * @param category The category of the requirement (e.g., "Dietary", "Religious").
   * @param option The specific option for the requirement (e.g., "Halal", "Kosher").
   * @throws IllegalArgumentException if the victim already has a requirement for the specified category.
   * @throws RuntimeException if there is an error inserting the requirement into the database.
   */
  public void insertRequirement(int victimId, String category, String option) {
    String query =
      "INSERT INTO CulturalRequirement (victim_id, requirement_category, requirement_option) VALUES (?, ?, ?)";

    try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
      stmt.setInt(1, victimId);
      stmt.setString(2, category);
      stmt.setString(3, option);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Could not insert cultural requirement.", e);
    }
  }

  /**
   * Updates an existing cultural requirement for a victim in the database.
   * 
   * @param victimId The ID of the victim for whom the requirement is being updated.
   * @param category The category of the requirement (e.g., "Dietary", "Religious").
   * @param newOption The new option for the requirement (e.g., "Halal", "Kosher").
   * @throws RuntimeException if there is an error updating the requirement in the database.
   */
  public void updateRequirement(
    int victimId,
    String category,
    String newOption
  ) {
    String query =
      "UPDATE CulturalRequirement SET requirement_option = ? WHERE victim_id = ? AND requirement_category = ?";

    try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
      stmt.setString(1, newOption);
      stmt.setInt(2, victimId);
      stmt.setString(3, category);

      int rows = stmt.executeUpdate();
      if (rows == 0) {
        throw new RuntimeException(
          "No cultural requirement found for victim id " +
            victimId +
            " and category " +
            category
        );
      }
    } catch (SQLException e) {
      throw new RuntimeException("Could not update cultural requirement.", e);
    }
  }

  /**
   * Deletes a cultural requirement for a victim from the database.
   * 
   * @param victimId The ID of the victim for whom the requirement is being deleted.
   * @param category The category of the requirement to be deleted (e.g., "Dietary", "Religious").
   * @throws IllegalArgumentException if no requirement is found for the victim and category.
   * @throws RuntimeException if there is an error deleting the requirement from the database.
   */
  public void deleteRequirement(int victimId, String category) {
    String query =
      "DELETE FROM CulturalRequirement WHERE victim_id = ? AND requirement_category = ?";

    try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
      stmt.setInt(1, victimId);
      stmt.setString(2, category);

      int rows = stmt.executeUpdate();
      if (rows == 0) {
        throw new RuntimeException(
          "No cultural requirement found for victim id " +
            victimId +
            " and category " +
            category
        );
      }
    } catch (SQLException e) {
      throw new RuntimeException("Could not delete cultural requirement.", e);
    }
  }

  /**
   * Deletes all cultural requirements for a victim from the database. This is used for hard deletion of a victim.
   * 
   * @param victimId The ID of the victim for whom all requirements are being deleted.
   * @throws RuntimeException if there is an error deleting the requirements from the database.
   */
  public void deleteAllRequirementsForVictim(int victimId) {
    String query = "DELETE FROM CulturalRequirement WHERE victim_id = ?";

    try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
      stmt.setInt(1, victimId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(
        "Failed to delete cultural requirements for victim.",
        e
      );
    }
  }

  // getters
  public List<CulturalRequirement> getRequirementsForVictim(int victimId) {
    String query = """
                      SELECT cr.victim_id, cr.requirement_category, cr.requirement_option
                      FROM CulturalRequirement
                      JOIN DisasterVictim dv ON cr.victim_id = dv.person_id
                      WHERE cr.victim_id = ? AND dv.is_soft_deleted = FALSE
                      ORDER BY cr.requirement_category
                  """;

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
      throw new RuntimeException(
        "Could not get cultural requirements for victim.",
        e
      );
    }

    return requirements;
  }

  /**
   * Checks if a victim already has a cultural requirement for a specific category in the database.
   * This is used to prevent duplicate entries for the same category.
   * 
   * @param victimId The ID of the victim being checked.
   * @param category The category of the requirement being checked (e.g., "Dietary", "Religious").
   * @return true if the victim already has a requirement for the specified category, false otherwise.
   * @throws RuntimeException if there is an error checking the requirement in the database.
   */
  public boolean victimHasRequirement(int victimId, String category) {
    String query =
      "SELECT 1 FROM CulturalRequirement WHERE victim_id = ? AND requirement_category = ?";

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
