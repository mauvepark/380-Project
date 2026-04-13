package edu.ucalgary.oop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class to manage inquiries related to disaster victims. 
 * This class provides methods to add, update, delete, and retrieve inquiries from the database. 
 * It uses the InquiryRepository for database operations and ActionLogger to log all actions performed on inquiries.
 */
public class InquiryService {
    private final InquiryRepository repository;
    private final ActionLogger logger;

    // constructor
    public InquiryService() {
        this.repository = new InquiryRepository(DatabaseManager.getInstance().getConnection());
        this.logger = ActionLogger.getInstance();
    }

    public InquiryService(InquiryRepository repository, ActionLogger logger) {
        this.repository = repository;
        this.logger = logger;
    }

    /**
     * Adds a new inquiry to the database with the provided inquirer, subject person, inquiry date, and details.
     * Validates the input parameters and logs the action after successfully adding the inquiry.
     * 
     * @param inquirer the Person object representing the inquirer (required)
     * @param subjectPerson the Person object representing the subject of the inquiry (optional, can be null)
     * @param inquiryDate the date and time when the inquiry was made (required, cannot be in the future)
     * @param details additional details about the inquiry (required, cannot be blank)
     * @return an Inquiry object representing the newly created inquiry
     * @throws IllegalArgumentException if any of the required parameters are invalid
     */
    public Inquiry addInquiry(Person inquirer, Person subjectPerson, LocalDateTime inquiryDate, String details) {
        if (inquirer == null) {
            throw new IllegalArgumentException("Inquirer cannot be null.");
        }
        if (inquiryDate == null) {
            throw new IllegalArgumentException("Inquiry date cannot be null.");
        }
        if (inquiryDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Inquiry date cannot be in the future.");
        }
        if (details == null || details.isBlank()) {
            throw new IllegalArgumentException("Details cannot be blank.");
        }

        int inquiryId = repository.addInquiry(inquirer.getId(), subjectPerson == null ? null : subjectPerson.getId(), inquiryDate, details);

        Inquiry inquiry = new Inquiry(inquiryId, inquirer, subjectPerson, inquiryDate, details);

        String subjectText = subjectPerson == null ? "Unknown" : subjectPerson.getFirstName() + " " + subjectPerson.getLastName();

        logger.log("ADDED", "inquiry " + inquiryId + " | Inquirer: " + inquirer.getFirstName() + " " + inquirer.getLastName() + " | Subject: " + subjectText);

        return inquiry;
    }

    /**
     * Updates the details of an existing inquiry. 
     * Validates the input parameters and logs the action after successfully updating the inquiry.
     * 
     * @param inquiry the Inquiry object representing the inquiry to update
     * @param newDetails the new details for the inquiry
     * @throws IllegalArgumentException if the inquiry is null or if the new details are invalid
     */
    public void updateInquiryDetails(Inquiry inquiry, String newDetails) {
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry cannot be null.");
        }
        if (newDetails == null || newDetails.isBlank()) {
            throw new IllegalArgumentException("Details cannot be blank.");
        }

        String oldDetails = inquiry.getDetails();

        repository.updateInquiryDetails(inquiry.getId(), newDetails);
        inquiry.setDetails(newDetails);

        logger.log("UPDATED", "inquiry " + inquiry.getId() + " | Details: " + oldDetails + " -> " + newDetails);
    }

    /**
     * Updates the subject person of an existing inquiry.
     * Validates the input parameters and logs the action after successfully updating the inquiry.
     * 
     * @param inquiry the Inquiry object representing the inquiry to update
     * @param newSubjectPerson the new Person object representing the subject of the inquiry (can be null to set subject to null)
     * @throws IllegalArgumentException if the inquiry is null
     */
    public void updateInquirySubject(Inquiry inquiry, Person newSubjectPerson) {
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry cannot be null.");
        }

        String oldSubject = inquiry.getSubjectPerson() == null ? "Unknown" : inquiry.getSubjectPerson().getFirstName() + " " + inquiry.getSubjectPerson().getLastName();

        String newSubject = newSubjectPerson == null ? "Unknown" : newSubjectPerson.getFirstName() + " " + newSubjectPerson.getLastName();

        repository.updateInquirySubject(
            inquiry.getId(),
            newSubjectPerson == null ? null : newSubjectPerson.getId()
        );

        inquiry.setSubjectPerson(newSubjectPerson);

        logger.log("UPDATED", "inquiry " + inquiry.getId() + " | Subject: " + oldSubject + " -> " + newSubject
        );
    }

    /**
     * Deletes an existing inquiry from the database. 
     * Validates the input parameter and logs the action after successfully deleting the inquiry.
     * 
     * @param inquiry the Inquiry object representing the inquiry to delete
     * @throws IllegalArgumentException if the inquiry is null
     */
    public void deleteInquiry(Inquiry inquiry) {
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry cannot be null.");
        }

        repository.deleteInquiry(inquiry.getId());

        logger.log("DELETED", "inquiry " + inquiry.getId() + " | Inquirer: " + inquiry.getInquirer().getFirstName() + " " + inquiry.getInquirer().getLastName());
    }

    /**
     * Retrieves all inquiries from the database, including the inquirer and subject person names. 
     * The results are ordered by inquiry date (newest first) and then by ID.
     * 
     * @return a list of all inquiries
     * @throws RuntimeException if there is an error retrieving the inquiries from the database
     */
    public List<Inquiry> getAllInquiries() {
        List<Inquiry> inquiries = new ArrayList<>();

        try (ResultSet rs = repository.getAllInquiries()) {
            while (rs.next()) {
                inquiries.add(mapRowToInquiry(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load inquiries.", e);
        }

        return inquiries;
    }

    /** 
     * Retrieves all inquiries made by a specific inquirer from the database, including the inquirer and subject person names.
     * 
     * @param inquirerId the ID of the inquirer for whom to retrieve inquiries
     * @return a list of inquiries made by the specified inquirer
     * @throws IllegalArgumentException if the inquirer ID is invalid
     * @throws RuntimeException if there is an error retrieving the inquiries from the database
     */
    public List<Inquiry> getInquiriesByInquirer(int inquirerId) {
        if (inquirerId <= 0) {
            throw new IllegalArgumentException("Inquirer ID must be positive.");
        }

        List<Inquiry> inquiries = new ArrayList<>();

        try (ResultSet rs = repository.getInquiriesByInquirer(inquirerId)) {
            while (rs.next()) {
                inquiries.add(mapRowToInquiry(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load inquiries for inquirer.", e);
        }

        return inquiries;
    }

    /**
     * Retrieves all inquiries about a specific subject person from the database, including the inquirer and subject person names.
     * The results are ordered by inquiry date (newest first) and then by ID.
     * 
     * @param subjectPersonId the ID of the subject person for whom to retrieve inquiries
     * @return a list of inquiries about the specified subject person
     * @throws IllegalArgumentException if the subject person ID is invalid
     * @throws RuntimeException if there is an error retrieving the inquiries from the database
     */
    public List<Inquiry> getInquiriesBySubjectPerson(int subjectPersonId) {
        if (subjectPersonId <= 0) {
            throw new IllegalArgumentException("Subject person ID must be positive.");
        }

        List<Inquiry> inquiries = new ArrayList<>();

        try (ResultSet rs = repository.getInquiriesBySubjectPerson(subjectPersonId)) {
            while (rs.next()) {
                inquiries.add(mapRowToInquiry(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load inquiries for subject person.", e);
        }

        return inquiries;
    }

    /**
     * Retrieves a specific inquiry from the database based on its ID, including the inquirer and subject person names.
     * 
     * @param inquiryId the ID of the inquiry to retrieve
     * @return an Inquiry object representing the retrieved inquiry, or null if no inquiry is found with the given ID
     * @throws IllegalArgumentException if the inquiry ID is invalid
     * @throws RuntimeException if there is an error retrieving the inquiry from the database
     */
    public Inquiry getInquiryById(int inquiryId) {
        if (inquiryId <= 0) {
            throw new IllegalArgumentException("Inquiry ID must be positive.");
        }

        try (ResultSet rs = repository.getInquiryById(inquiryId)) {
            if (rs.next()) {
                return mapRowToInquiry(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load inquiry.", e);
        }

        return null;
    }

    /** 
     * Helper method to map a ResultSet row to an Inquiry object. 
     * This method assumes that the ResultSet is already positioned at the correct row.
     * 
     * @param rs the ResultSet containing the inquiry data, including inquirer and subject person names
     * @return an Inquiry object representing the data in the current row of the ResultSet
     */
    private Inquiry mapRowToInquiry(ResultSet rs) throws SQLException {
        int inquiryId = rs.getInt("id");

        Person inquirer = new Person(rs.getInt("inquirer_id"), rs.getString("inquirer_first_name"), rs.getString("inquirer_last_name"), null);

        int subjectId = rs.getInt("subject_person_id");
        Person subjectPerson = null;
        if (!rs.wasNull()) {
                subjectPerson = new Person(
                subjectId,
                rs.getString("subject_first_name"),
                rs.getString("subject_last_name"),
                null
            );
        }

        Timestamp inquiryTimestamp = rs.getTimestamp("inquiry_date");
        LocalDateTime inquiryDate = inquiryTimestamp.toLocalDateTime();

        String details = rs.getString("details");

        return new Inquiry(inquiryId, inquirer, subjectPerson, inquiryDate, details);
    }
}