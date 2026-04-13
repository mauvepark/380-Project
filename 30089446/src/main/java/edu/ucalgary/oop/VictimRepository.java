package edu.ucalgary.oop;

import java.sql.*;

/**
 * Repository class for managing victims in the database.
 * This class handles inserting, updating, deleting, and checking victim records.
 */
public class VictimRepository {
    private final Connection dbConnect;

    /**
     * Constructor for VictimRepository.
     *
     * @param dbConnect the database connection used for victim queries
     */
    public VictimRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    /**
     * Inserts a new person into the Person table.
     *
     * @param firstName the first name
     * @param lastName the last name
     * @param comments extra comments
     * @return the ID of the new person
     * @throws RuntimeException if the person cannot be inserted
     */
    public int insertPerson(String firstName, String lastName, String comments) {
        String query = "INSERT INTO Person (first_name, last_name, comments) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, comments);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert person.", e);
        }

        throw new RuntimeException("Could not insert person.");
    }

    /**
     * Inserts a new disaster victim into the DisasterVictim table.
     *
     * @param personId the related person ID
     * @param dateOfBirth the victim's birthdate
     * @param approximateAge the victim's approximate age
     * @param gender the victim's gender
     * @param entryDate the entry date
     * @param locationId the location ID
     * @throws RuntimeException if the victim cannot be inserted
     */
    public void insertDisasterVictim(int personId, java.time.LocalDate dateOfBirth, Integer approximateAge, String gender, java.time.LocalDate entryDate, Integer locationId) {
        String query = "INSERT INTO DisasterVictim (person_id, date_of_birth, approximate_age, gender, entry_date, location_id, is_soft_deleted) VALUES (?, ?, ?, ?, ?, ?, FALSE)";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, personId);

            if (dateOfBirth == null) {
                stmt.setNull(2, java.sql.Types.DATE);
            } else {
                stmt.setDate(2, Date.valueOf(dateOfBirth));
            }

            if (approximateAge == null) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, approximateAge);
            }

            stmt.setString(4, gender);
            stmt.setDate(5, Date.valueOf(entryDate));

            if (locationId == null) {
                stmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(6, locationId);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert disaster victim.", e);
        }
    }

    /**
     * Updates a victim's name in the Person table.
     *
     * @param personId the person ID
     * @param newFirstName the new first name
     * @param newLastName the new last name
     * @throws RuntimeException if the victim name cannot be updated
     */
    public void updateVictimName(int personId, String newFirstName, String newLastName) {
        String query = "UPDATE Person SET first_name = ?, last_name = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, newFirstName);
            stmt.setString(2, newLastName);
            stmt.setInt(3, personId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No person found with id " + personId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update person name.", e);
        }
    }

    /**
     * Updates a victim's age information.
     *
     * @param personId the person ID
     * @param dateOfBirth the new birthdate
     * @param approximateAge the new approximate age
     * @throws RuntimeException if the age information cannot be updated
     */
    public void updateVictimAgeInfo(int personId, java.time.LocalDate dateOfBirth, Integer approximateAge) {
        String query = "UPDATE DisasterVictim SET date_of_birth = ?, approximate_age = ? WHERE person_id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            if (dateOfBirth == null) {
                stmt.setNull(1, java.sql.Types.DATE);
            } else {
                stmt.setDate(1, Date.valueOf(dateOfBirth));
            }

            if (approximateAge == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, approximateAge);
            }

            stmt.setInt(3, personId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No disaster victim found with id " + personId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update victim age information.", e);
        }
    }

    /**
     * Soft deletes a victim by marking them as deleted.
     *
     * @param personId the person ID
     * @throws RuntimeException if the victim cannot be soft deleted
     */
    public void softDeleteVictim(int personId) {
        String query = "UPDATE DisasterVictim SET is_soft_deleted = TRUE WHERE person_id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, personId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No disaster victim found with id " + personId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not soft delete victim.", e);
        }
    }

    /**
     * Hard deletes a victim and related records from the database.
     *
     * @param personId the person ID
     * @throws RuntimeException if the victim cannot be hard deleted
     */
    public void hardDeleteVictim(int personId) {
        String deleteInquiry = "DELETE FROM Inquiry WHERE inquirer_id = ? OR subject_person_id = ?";
        String deleteRelationships = "DELETE FROM FamilyRelationship WHERE person_one_id = ? OR person_two_id = ?";
        String deleteAllocatedSupplies = "DELETE FROM Supply WHERE victim_id = ?";
        String deletePerson = "DELETE FROM Person WHERE id = ?";
        String deleteMedicalRecords = "DELETE FROM MedicalRecord WHERE victim_id = ?";
        String deleteVictimSkill = "DELETE FROM VictimSkill WHERE victim_id = ?";
        String deleteCulturalRequirements = "DELETE FROM CulturalRequirement WHERE victim_id = ?";

        try {
            dbConnect.setAutoCommit(false);

            try (PreparedStatement stmt1 = dbConnect.prepareStatement(deleteInquiry);
                PreparedStatement stmt2 = dbConnect.prepareStatement(deleteRelationships);
                PreparedStatement stmt3 = dbConnect.prepareStatement(deleteAllocatedSupplies);
                PreparedStatement stmt4 = dbConnect.prepareStatement(deletePerson);
                PreparedStatement stmt5 = dbConnect.prepareStatement(deleteMedicalRecords);
                PreparedStatement stmt6 = dbConnect.prepareStatement(deleteVictimSkill);
                PreparedStatement stmt7 = dbConnect.prepareStatement(deleteCulturalRequirements)) {

                // delete inquiries
                stmt1.setInt(1, personId);
                stmt1.setInt(2, personId);
                stmt1.executeUpdate();

                // delete family relationships
                stmt2.setInt(1, personId);
                stmt2.setInt(2, personId);
                stmt2.executeUpdate();

                // delete supplies 
                stmt3.setInt(1, personId);
                stmt3.executeUpdate();

                // delete medical records
                stmt5.setInt(1, personId);
                stmt5.executeUpdate();

                // delete victim skills
                stmt6.setInt(1, personId);
                stmt6.executeUpdate();

                // delete cultural requirements
                stmt7.setInt(1, personId);
                stmt7.executeUpdate();

                // delete person record
                stmt4.setInt(1, personId);
                int rows = stmt4.executeUpdate();

                if (rows == 0) {
                    throw new RuntimeException("No person found with ID: " + personId);
                }

                dbConnect.commit();

            } catch (SQLException | RuntimeException e) {
                dbConnect.rollback();
                throw e;
            } finally {
                dbConnect.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Could not hard delete victim.", e);
        }
    }

    /**
     * Gets all active victims from the database.
     *
     * @return a ResultSet of active victims
     * @throws RuntimeException if the victims cannot be retrieved
     */
    public ResultSet getActiveVictims() {
        String query = "SELECT p.id, p.first_name, p.last_name, p.comments, dv.date_of_birth, dv.approximate_age, dv.gender, dv.entry_date, dv.location_id FROM Person p JOIN DisasterVictim dv ON p.id = dv.person_id WHERE dv.is_soft_deleted = FALSE ORDER BY p.id";

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get active victims.", e);
        }
    }

    /**
     * Checks if a person is an active victim.
     *
     * @param personId the person ID
     * @return true if the victim exists and is active, false otherwise
     * @throws RuntimeException if the victim status cannot be checked
     */
    public boolean isActiveVictim(int personId) {
        String query = "SELECT 1 FROM DisasterVictim WHERE person_id = ? AND is_soft_deleted = FALSE";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, personId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check victim status.", e);
        }
    }
}
