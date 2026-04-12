package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDateTime;

public class InquiryRepository {
    private final Connection dbConnect;

    // constructor
    public InquiryRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    // add new inquiry
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

    // update inquiry details
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

    // update subject person
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

    // delete inquiry
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

    // get all inquiries
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

    // get inquiries by inquirer
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

    // get inquiries by subject person
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

    // get single inquiry by id
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