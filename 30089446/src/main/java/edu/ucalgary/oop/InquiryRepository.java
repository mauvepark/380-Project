package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Repository class for managing inquiries in the disaster victim management system. 
 * Provides methods to add, update, delete, and retrieve inquiries from the database. 
 * Each method interacts with the database using SQL queries and handles any SQL exceptions that may occur.
 */
public class InquiryRepository {
    private final Connection dbConnect;

    // constructor
    public InquiryRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    /**
     * Adds a new inquiry to the database with the provided inquirer ID, subject person ID, inquiry date, and details.
     * 
     * @param inquirerId the ID of the person making the inquiry (required)
     * @param subjectPersonId the ID of the person who is the subject of the inquiry (optional, can be null)
     * @param inquiryDate the date and time when the inquiry was made (required)
     * @param details additional details about the inquiry (required)
     * @return the ID of the newly created inquiry
     * @throws RuntimeException if there is an error inserting the inquiry into the database
     */
    public int addInquiry(int inquirerId, Integer subjectPersonId, LocalDateTime inquiryDate, String details) {
        String query = "INSERT INTO Inquiry (inquirer_id, subject_person_id, inquiry_date, details) VALUES (?, ?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, inquirerId);

            if (subjectPersonId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, subjectPersonId);
            }

            stmt.setTimestamp(3, Timestamp.valueOf(inquiryDate));
            stmt.setString(4, details);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert inquiry.", e);
        }

        throw new RuntimeException("Could not insert inquiry.");
    }

    /**
     * Updates the details of an existing inquiry in the database, identified by its ID.
     * 
     * @param inquiryId the ID of the inquiry to update
     * @param newDetails the new details for the inquiry
     * @throws RuntimeException if there is an error updating the inquiry in the database or if no inquiry is found with the given ID
     */
    public void updateInquiryDetails(int inquiryId, String newDetails) {
        String query = "UPDATE Inquiry SET details = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, newDetails);
            stmt.setInt(2, inquiryId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No inquiry found with id " + inquiryId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update inquiry details.", e);
        }
    }

    /**
     * Updates the subject person of an existing inquiry in the database, identified by its ID. 
     * The subject person can be set to null if there is no subject for the inquiry.
     * 
     * @param inquiryId the ID of the inquiry to update
     * @param subjectPersonId the ID of the new subject person for the inquiry, or null to set the subject person to null
     * @throws RuntimeException if there is an error updating the inquiry in the database or if no inquiry is found with the given ID
     */
    public void updateInquirySubject(int inquiryId, Integer subjectPersonId) {
        String query = "UPDATE Inquiry SET subject_person_id = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            if (subjectPersonId == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, subjectPersonId);
            }

            stmt.setInt(2, inquiryId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No inquiry found with id " + inquiryId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update inquiry subject.", e);
        }
    }

    /**
     * Deletes an inquiry from the database based on its ID. This method checks if the inquiry exists before attempting to delete it.
     * 
     * @param inquiryId the ID of the inquiry to be deleted
     * @throws RuntimeException if there is an error deleting the inquiry from the database
     */
    public void deleteInquiry(int inquiryId) {
        String query = "DELETE FROM Inquiry WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, inquiryId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No inquiry found with id " + inquiryId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete inquiry.", e);
        }
    }

    /**
     * Retrieves all inquiries from the database, including the inquirer and subject person names. The results are ordered by inquiry date (newest first) and then by ID.
     * 
     * @return a ResultSet containing all inquiries with inquirer and subject person names
     * @throws RuntimeException if there is an error retrieving the inquiries from the database
     */
    public ResultSet getAllInquiries() {
        String query = """
                        SELECT i.id, i.inquirer_id, i.subject_person_id, i.inquiry_date, i.details, p1.first_name AS inquirer_first_name, p1.last_name AS inquirer_last_name, p2.first_name AS subject_first_name, p2.last_name AS subject_last_name
                        FROM Inquiry i
                        JOIN Person p1 ON i.inquirer_id = p1.id
                        LEFT JOIN Person p2 ON i.subject_person_id = p2.id
                        LEFT JOIN DisasterVictim dv1 ON i.inquirer_id = dv1.person_id
                        LEFT JOIN DisasterVictim dv2 ON i.subject_person_id = dv2.person_id
                        WHERE (dv1.person_id IS NULL OR dv1.is_soft_deleted = FALSE)
                        AND (dv2.person_id IS NULL OR dv2.is_soft_deleted = FALSE)
                        ORDER BY i.inquiry_date DESC, i.id
                    """;

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get inquiries.", e);
        }
    }

    /**
     * Retrieves all inquiries made by a specific inquirer from the database, including the inquirer and subject person names. The results are ordered by inquiry date (newest first) and then by ID.
     * 
     * @param inquirerId the ID of the inquirer for whom to retrieve inquiries
     * @return a ResultSet containing all inquiries made by the specified inquirer with inquirer and subject person names
     * @throws RuntimeException if there is an error retrieving the inquiries from the database
     */
    public ResultSet getInquiriesByInquirer(int inquirerId) {
        String query = """
                        SELECT i.id, i.inquirer_id, i.subject_person_id, i.inquiry_date, i.details, p1.first_name AS inquirer_first_name, p1.last_name AS inquirer_last_name, p2.first_name AS subject_first_name, p2.last_name AS subject_last_name
                        FROM Inquiry i
                        JOIN Person p1 ON i.inquirer_id = p1.id
                        LEFT JOIN Person p2 ON i.subject_person_id = p2.id
                        LEFT JOIN DisasterVictim dv1 ON i.inquirer_id = dv1.person_id
                        LEFT JOIN DisasterVictim dv2 ON i.subject_person_id = dv2.person_id
                        WHERE i.inquirer_id = ?
                        AND (dv1.person_id IS NULL OR dv1.is_soft_deleted = FALSE)
                        AND (dv2.person_id IS NULL OR dv2.is_soft_deleted = FALSE)
                        ORDER BY i.inquiry_date DESC, i.id
                    """;

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            stmt.setInt(1, inquirerId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get inquiries for inquirer.", e);
        }
    }

    /**
     * Retrieves all inquiries about a specific subject person from the database, including the inquirer and subject person names. 
     * The results are ordered by inquiry date (newest first) and then by ID.
     * 
     * @param subjectPersonId the ID of the subject person for whom to retrieve inquiries
     * @return a ResultSet containing all inquiries about the specified subject person with inquirer and subject person names
     * @throws RuntimeException if there is an error retrieving the inquiries from the database
     */
    public ResultSet getInquiriesBySubjectPerson(int subjectPersonId) {
        String query = """
                        SELECT i.id, i.inquirer_id, i.subject_person_id, i.inquiry_date, i.details,
                            p1.first_name AS inquirer_first_name, p1.last_name AS inquirer_last_name,
                            p2.first_name AS subject_first_name, p2.last_name AS subject_last_name
                        FROM Inquiry i
                        JOIN Person p1 ON i.inquirer_id = p1.id
                        LEFT JOIN Person p2 ON i.subject_person_id = p2.id
                        LEFT JOIN DisasterVictim dv1 ON i.inquirer_id = dv1.person_id
                        LEFT JOIN DisasterVictim dv2 ON i.subject_person_id = dv2.person_id
                        WHERE i.subject_person_id = ?
                        AND (dv1.person_id IS NULL OR dv1.is_soft_deleted = FALSE)
                        AND (dv2.person_id IS NULL OR dv2.is_soft_deleted = FALSE)
                        ORDER BY i.inquiry_date DESC, i.id
                    """;

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            stmt.setInt(1, subjectPersonId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get inquiries for subject person.", e);
        }
    }

    /**
     * Retrieves a specific inquiry from the database based on its ID, including the inquirer and subject person names.
     * 
     * @param inquiryId the ID of the inquiry to retrieve
     * @return a ResultSet containing the inquiry with inquirer and subject person names
     * @throws RuntimeException if there is an error retrieving the inquiry from the database
     */
    public ResultSet getInquiryById(int inquiryId) {
        String query = """
                        SELECT i.id, i.inquirer_id, i.subject_person_id, i.inquiry_date, i.details, p1.first_name AS inquirer_first_name, p1.last_name AS inquirer_last_name, p2.first_name AS subject_first_name, p2.last_name AS subject_last_name
                        FROM Inquiry i
                        JOIN Person p1 ON i.inquirer_id = p1.id
                        LEFT JOIN Person p2 ON i.subject_person_id = p2.id
                        LEFT JOIN DisasterVictim dv1 ON i.inquirer_id = dv1.person_id
                        LEFT JOIN DisasterVictim dv2 ON i.subject_person_id = dv2.person_id
                        WHERE i.id = ?
                        AND (dv1.person_id IS NULL OR dv1.is_soft_deleted = FALSE)
                        AND (dv2.person_id IS NULL OR dv2.is_soft_deleted = FALSE)
                    """;

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            stmt.setInt(1, inquiryId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get inquiry.", e);
        }
    }
}