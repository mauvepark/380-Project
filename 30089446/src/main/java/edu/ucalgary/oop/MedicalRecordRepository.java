package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;

/**
 * The MedicalRecordRepository class provides methods for performing CRUD operations on medical records in the database.
 * It allows for inserting new medical records, updating existing records, deleting records, and retrieving records by ID or by victim. 
 * The class uses prepared statements to interact with the database and includes error handling.
 * 
 */
public class MedicalRecordRepository {
    private final Connection dbConnect;

    /**
     * Constructor for MedicalRecordRepository. Initializes the repository with a database connection.
     * 
     * @param dbConnect the database connection to be used for all operations in this repository (must not be null)
     */
    public MedicalRecordRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    /**
     * Inserts a new medical record for a victim into the database.
     * 
     * @param victimId the ID of the victim associated with this medical record (must be positive)
     * @param treatmentDetails details about the treatment provided (cannot be null or blank)
     * @param treatmentDate the date when the treatment was provided (cannot be null or in the future)
     * @param locationId the ID of the location where the treatment was provided (can be null if not applicable)
     * @return the ID of the newly inserted medical record
     * @throws RuntimeException if there is an error inserting the medical record into the database
     */
    public int insertMedicalRecord(int victimId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
        String query = """
                        INSERT INTO MedicalRecord (victim_id, treatment_details, treatment_date, location_id)
                        VALUES (?, ?, ?, ?)
                        RETURNING id
                       """;

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.setString(2, treatmentDetails);
            stmt.setDate(3, Date.valueOf(treatmentDate));

            if (locationId == null) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, locationId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert medical record.", e);
        }

        throw new RuntimeException("Could not insert medical record.");
    }

    /**
     * Updates an existing medical record in the database with new treatment details, date, and location.
     *  
     * @param recordId the ID of the medical record to update (must be positive)
     * @param treatmentDetails the new details about the treatment provided (cannot be null or blank)
     * @param treatmentDate the new date when the treatment was provided (cannot be null or in the future)
     * @param locationId the new ID of the location where the treatment was provided (can be null if not applicable)
     * @throws RuntimeException if there is an error updating the medical record in the database or
     */
    public void updateMedicalRecord(int recordId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
        String query = "UPDATE MedicalRecord SET treatment_details = ?, treatment_date = ?, location_id = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, treatmentDetails);
            stmt.setDate(2, Date.valueOf(treatmentDate));

            if (locationId == null) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, locationId);
            }

            stmt.setInt(4, recordId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No medical record found with id " + recordId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update medical record.", e);
        }
    }

    /**
     * Deletes a medical record from the database by its ID.
     * 
     * @param recordId the ID of the medical record to delete (must be positive)
     * @throws RuntimeException if there is an error deleting the medical record from the database or
     */
    public void deleteMedicalRecord(int recordId) {
        String query = "DELETE FROM MedicalRecord WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, recordId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No medical record found with id " + recordId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete medical record.", e);
        }
    }

    /**
     * Retrieves a medical record from the database by its ID.
     * 
     * @param recordId the ID of the medical record to retrieve (must be positive)
     * @return a ResultSet containing the medical record with the specified ID, or an empty ResultSet if no record is found
     * @throws RuntimeException if there is an error retrieving the medical record from the database
     */
    public ResultSet getMedicalRecordById(int recordId) {
        String query = "SELECT id, victim_id, treatment_details, treatment_date, location_id FROM MedicalRecord WHERE id = ?";

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            stmt.setInt(1, recordId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get medical record.", e);
        }
    }

    /**
     * Retrieves all medical records for a specific victim from the database, ordered by treatment date (most recent first) and then by record ID.
     * 
     * @param victimId the ID of the victim for whom to retrieve medical records (must be positive)
     * @return a ResultSet containing all medical records for the specified victim, ordered by treatment date (most recent first) and then by record ID, or an empty ResultSet if no records are found
     * @throws RuntimeException if there is an error retrieving the medical records from the database
     */
    public ResultSet getMedicalRecordsForVictim(int victimId) {
        String query = """
                        SELECT id, victim_id, treatment_details, treatment_date, location_id
                        FROM MedicalRecord
                        JOIN DisasterVictim dv ON mr.victim_id = dv.person_id
                        WHERE victim_id = ? WHERE mr.victim_id = ? AND dv.is_soft_deleted = FALSE
                        ORDER BY treatment_date DESC, id
                       """;

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            stmt.setInt(1, victimId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get medical records for victim.", e);
        }
    }

    /**
    * Checks if a medical record with the given ID exists in the database.
    * 
    * @param recordId the ID of the medical record to check for existence (must be positive)
    * @return true if a medical record with the given ID exists, false otherwise
    * @throws IllegalArgumentException if the recordId is not positive
    */
    public boolean medicalRecordExists(int recordId) {
        String query = "SELECT 1 FROM MedicalRecord WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, recordId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check medical record.", e);
        }
    }
}