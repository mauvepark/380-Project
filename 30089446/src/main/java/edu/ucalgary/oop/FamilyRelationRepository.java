package edu.ucalgary.oop;

import java.sql.*;

/**
 * Repository class for managing family relationships between people in the disaster victim management system.
 * This class provides methods to insert, update, delete, and retrieve family relationships from the database.
 * Each family relationship links two people together with a specified relationship type (e.g., "Parent", "Sibling").
 */
public class FamilyRelationRepository {
    private final Connection dbConnect;

    // constructor
    public FamilyRelationRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    /**
     * Inserts a new family relationship into the database linking two people with a specified relationship type.
     * The method returns the ID of the newly created family relationship record.
     * 
     * @param personOneId the ID of the first person in the relationship
     * @param personTwoId the ID of the second person in the relationship
     * @param relationshipType the type of family relationship (e.g., "Parent", "Sibling")
     * @return the ID of the newly created family relationship record
     * @throws RuntimeException if there is an error inserting the family relationship into the database
     */
    public int insertFamilyRelation(int personOneId, int personTwoId, String relationshipType) {
        String query = "INSERT INTO FamilyRelationship (person_one_id, person_two_id, relationship_type) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, personOneId);
            stmt.setInt(2, personTwoId);
            stmt.setString(3, relationshipType);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert family relation.", e);
        }

        throw new RuntimeException("Could not insert family relation.");
    }

    /**
     * Updates the relationship type of an existing family relationship in the database based on the provided relationship ID.
     * 
     * @param relationId the ID of the family relationship to update
     * @param newRelationshipType the new relationship type to set for the family relationship
     * @throws RuntimeException if there is an error updating the family relationship in the database or if no family relationship is found with the given ID.
     */
    public void updateRelationshipType(int relationId, String newRelationshipType) {
        String query = "UPDATE FamilyRelationship SET relationship_type = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, newRelationshipType);
            stmt.setInt(2, relationId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No family relation found with id " + relationId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update family relation.", e);
        }
    }

    /**
     * Deletes an existing family relationship from the database based on the provided relationship ID.
     * 
     * @param relationId the ID of the family relationship to delete
     * @throws RuntimeException if there is an error deleting the family relationship from the database or if no family relationship is found with the given ID.
     */
    public void deleteFamilyRelation(int relationId) {
        String query = "DELETE FROM FamilyRelationship WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, relationId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No family relation found with id " + relationId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete family relation.", e);
        }
    }

    /**
     * Retrieves all family relationships from the database, including the IDs and names of the two people involved and the type of relationship.
     * 
     * @return a ResultSet containing all family relationships
     * @throws RuntimeException if there is an error retrieving the family relationships from the database.
     */
    public ResultSet getAllFamilyRelations() {
        String query = """
                        SELECT fr.id, fr.person_one_id, fr.person_two_id, fr.relationship_type, p1.first_name AS person_one_first_name, p1.last_name AS person_one_last_name, p2.first_name AS person_two_first_name, p2.last_name AS person_two_last_name
                        FROM FamilyRelationship fr
                        JOIN Person p1 ON fr.person_one_id = p1.id
                        JOIN Person p2 ON fr.person_two_id = p2.id
                        LEFT JOIN DisasterVictim dv1 ON fr.person_one_id = dv1.person_id
                        LEFT JOIN DisasterVictim dv2 ON fr.person_two_id = dv2.person_id
                        WHERE (dv1.person_id IS NULL OR dv1.is_soft_deleted = FALSE)
                        AND (dv2.person_id IS NULL OR dv2.is_soft_deleted = FALSE)
                        ORDER BY fr.id
                      """;

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get family relations.", e);
        }
    }

    /**
     * Retrieves all family relationships for a specific person from the database, including the IDs and names of the two people involved and the type of relationship.
     *
     * @param personId the ID of the person for whom to retrieve family relationships
     * @return a ResultSet containing the family relationships for the specified person
     * @throws RuntimeException if there is an error retrieving the family relationships from the database.
     */
    public ResultSet getFamilyRelationsForPerson(int personId) {
        String query = """
                        SELECT fr.id, fr.person_one_id, fr.person_two_id, fr.relationship_type, p1.first_name AS person_one_first_name, p1.last_name AS person_one_last_name, p2.first_name AS person_two_first_name, p2.last_name AS person_two_last_name
                        FROM FamilyRelationship fr
                        JOIN Person p1 ON fr.person_one_id = p1.id
                        JOIN Person p2 ON fr.person_two_id = p2.id
                        LEFT JOIN DisasterVictim dv1 ON fr.person_one_id = dv1.person_id
                        LEFT JOIN DisasterVictim dv2 ON fr.person_two_id = dv2.person_id
                        WHERE (fr.person_one_id = ? OR fr.person_two_id = ?)
                        AND (dv1.person_id IS NULL OR dv1.is_soft_deleted = FALSE)
                        AND (dv2.person_id IS NULL OR dv2.is_soft_deleted = FALSE)
                        ORDER BY fr.id
                      """;

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            stmt.setInt(1, personId);
            stmt.setInt(2, personId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get family relations for person.", e);
        }
    }

    /**
     * Retrieves a specific family relationship from the database by its ID, including the IDs and names of the two people involved and the type of relationship.
     *
     * @param relationId the ID of the family relationship to retrieve
     * @return a ResultSet containing the family relationship with the specified ID
     * @throws RuntimeException if there is an error retrieving the family relationship from the database or if no family relationship is found with the given ID.
     */
    public ResultSet getFamilyRelationById(int relationId) {
        String query = """
                        SELECT fr.id, fr.person_one_id, fr.person_two_id, fr.relationship_type, p1.first_name AS person_one_first_name, p1.last_name AS person_one_last_name, p2.first_name AS person_two_first_name, p2.last_name AS person_two_last_name
                        FROM FamilyRelationship fr
                        JOIN Person p1 ON fr.person_one_id = p1.id
                        JOIN Person p2 ON fr.person_two_id = p2.id
                        LEFT JOIN DisasterVictim dv1 ON fr.person_one_id = dv1.person_id
                        LEFT JOIN DisasterVictim dv2 ON fr.person_two_id = dv2.person_id
                        WHERE fr.id = ?
                        AND (dv1.person_id IS NULL OR dv1.is_soft_deleted = FALSE)
                        AND (dv2.person_id IS NULL OR dv2.is_soft_deleted = FALSE)
                      """;

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            stmt.setInt(1, relationId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get family relation.", e);
        }
    }

    /**
     * Checks if a family relationship exists between two people with a specific type.
     *
     * @param personOneId the ID of the first person
     * @param personTwoId the ID of the second person
     * @param relationshipType the type of relationship
     * @return true if the family relationship exists, false otherwise
     * @throws RuntimeException if there is an error checking the family relationship.
     */
    public boolean relationExists(int personOneId, int personTwoId, String relationshipType) {
        String query = "SELECT 1 FROM FamilyRelationship WHERE person_one_id = ? AND person_two_id = ? AND relationship_type = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, personOneId);
            stmt.setInt(2, personTwoId);
            stmt.setString(3, relationshipType);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check family relation.", e);
        }
    }
}