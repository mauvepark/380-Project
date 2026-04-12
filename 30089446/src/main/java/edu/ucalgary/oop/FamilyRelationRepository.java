package edu.ucalgary.oop;

import java.sql.*;

public class FamilyRelationRepository {
    private final Connection dbConnect;

    // constructor
    public FamilyRelationRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    // add family relation
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

    // update relationship type
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

    // delete family relation
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

    // get all family relations
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

    // get family relations for one person
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

    // get one relation by id
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

    // optional helper: prevent duplicate exact relation
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