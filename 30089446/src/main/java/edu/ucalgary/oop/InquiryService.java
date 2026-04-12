package edu.ucalgary.oop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // add a new inquiry
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

    // update inquiry details
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

    // update inquiry subject person
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

    // delete inquiry
    public void deleteInquiry(Inquiry inquiry) {
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry cannot be null.");
        }

        repository.deleteInquiry(inquiry.getId());

        logger.log("DELETED", "inquiry " + inquiry.getId() + " | Inquirer: " + inquiry.getInquirer().getFirstName() + " " + inquiry.getInquirer().getLastName());
    }

    // get all inquiries
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

    // get inquiries by inquirer
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

    // get inquiries by subject person
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

    // get one inquiry by id
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

    // map result to inquiry object
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