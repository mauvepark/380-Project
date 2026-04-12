package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;

public class MedicalRecordRepository {
    private final Connection dbConnect;

    // constructor
    public MedicalRecordRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    // add new medical record
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

    // update medical record
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

    // delete medical record
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

    // get medical record by id
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

    // get all medical records for one victim
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

    // check if record exists
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