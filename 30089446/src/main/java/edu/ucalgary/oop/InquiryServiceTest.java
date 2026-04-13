package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the InquiryService class.
 * These tests check inquiry creation, updates, deletion, and validation.
 */
public class InquiryServiceTest {

    private InquiryService service;
    private MockInquiryRepository mockRepository;
    private MockActionLogger mockLogger;

    @Before
    public void setUp() {
        mockRepository = new MockInquiryRepository();
        mockLogger = new MockActionLogger();
        service = new InquiryService(mockRepository, mockLogger);
    }

    @Test
    public void testAddInquiryValid() {
        Person inquirer = new Person(1, "Erwin", "Smith", null);
        Person subject = new Person(2, "Levi", "Ackerman", null);
        LocalDateTime inquiryDate = LocalDateTime.now().minusDays(1);

        Inquiry inquiry = service.addInquiry(
            inquirer,
            subject,
            inquiryDate,
            "In search of spouse"
        );

        assertNotNull("Inquiry should be made", inquiry);
        assertEquals("Inquiry ID should be assigned", 1, inquiry.getId());
        assertEquals("Inquirer should match", inquirer, inquiry.getInquirer());
        assertEquals("Subject person should match", subject, inquiry.getSubjectPerson());
        assertEquals("Comments should match", "In search of spouse", inquiry.getDetails());
    }

    @Test
    public void testAddInquiryWithNullSubjectValid() {
        Person inquirer = new Person(1, "Eren", "Jaeger", null);
        LocalDateTime inquiryDate = LocalDateTime.now().minusDays(1);

        Inquiry inquiry = service.addInquiry(
            inquirer,
            null,
            inquiryDate,
            "In search of family"
        );

        assertNotNull("Inquiry should be created", inquiry);
        assertNull("Subject should be null", inquiry.getSubjectPerson());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInquiryWithNullInquirerInvalid() {
        service.addInquiry(
            null,
            null,
            LocalDateTime.now().minusDays(1),
            "Details"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInquiryWithNullDateInvalid() {
        Person inquirer = new Person(1, "Hange", "Zoe", null);

        service.addInquiry(
            inquirer,
            null,
            null,
            "Details"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInquiryWithFutureDateInvalid() {
        Person inquirer = new Person(1, "Hange", "Zoe", null);

        service.addInquiry(
            inquirer,
            null,
            LocalDateTime.now().plusDays(1),
            "Details"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInquiryWithBlankDetailsInvalid() {
        Person inquirer = new Person(1, "Hange", "Zoe", null);

        service.addInquiry(
            inquirer,
            null,
            LocalDateTime.now().minusDays(1),
            "   "
        );
    }

    @Test
    public void testUpdateInquiryDetailsValid() {
        Inquiry inquiry = service.addInquiry(
            new Person(1, "Hange", "Zoe", null),
            null,
            LocalDateTime.now().minusDays(1),
            "Old test"
        );

        service.updateInquiryDetails(inquiry, "New test");

        assertEquals("Inquiry details should be updated", "New test", inquiry.getDetails());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInquiryDetailsNullInquiryInvalid() {
        service.updateInquiryDetails(null, "New test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInquiryDetailsBlankInvalid() {
        Inquiry inquiry = service.addInquiry(
            new Person(1, "Hange", "Zoe", null),
            null,
            LocalDateTime.now().minusDays(1),
            "Old test"
        );

        service.updateInquiryDetails(inquiry, "   ");
    }

    @Test
    public void testUpdateInquirySubjectValid() {
        Inquiry inquiry = service.addInquiry(
            new Person(1, "Hange", "Zoe", null),
            new Person(2, "Levi", "Azckerman", null),
            LocalDateTime.now().minusDays(1),
            "Looking for friend"
        );

        Person newSubject = new Person(3, "Erwin", "Smith", null);
        service.updateInquirySubject(inquiry, newSubject);

        assertEquals("Inquiry subject should be updated", newSubject, inquiry.getSubjectPerson());
    }

    @Test
    public void testUpdateInquirySubjectToNullValid() {
        Inquiry inquiry = service.addInquiry(
            new Person(1, "Hange", "Zoe", null),
            new Person(2, "Levi", "Azckerman", null),
            LocalDateTime.now().minusDays(1),
            "Looking for friend"
        );

        service.updateInquirySubject(inquiry, null);

        assertNull("Inquiry subject should be cleared", inquiry.getSubjectPerson());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInquirySubjectNullInquiryInvalid() {
        service.updateInquirySubject(null, new Person(2, "Mikasa", "Ackerman", null));
    }

    @Test
    public void testDeleteInquiryValid() {
        Inquiry inquiry = service.addInquiry(
            new Person(1, "Hange", "Zoe", null),
            null,
            LocalDateTime.now().minusDays(1),
            "Looking"
        );

        service.deleteInquiry(inquiry);

        assertNull("Deleted inquiry should no longer be stored", mockRepository.getMockInquiryById(inquiry.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteInquiryNullInvalid() {
        service.deleteInquiry(null);
    }

    private static class MockInquiryRepository extends InquiryRepository {
        private final Map<Integer, Inquiry> mockInquiries = new HashMap<>();
        private int nextId = 1;

        public MockInquiryRepository() {
            super(null);
        }

        @Override
        public int addInquiry(int inquirerId, Integer subjectPersonId, LocalDateTime inquiryDate, String details) {
            int id = nextId++;

            Person inquirer = new Person(inquirerId, "Inquirer", "Test", null);
            Person subject = null;
            if (subjectPersonId != null) {
                subject = new Person(subjectPersonId, "Subject", "Test", null);
            }

            mockInquiries.put(id, new Inquiry(id, inquirer, subject, inquiryDate, details));
            return id;
        }

        @Override
        public void updateInquiryDetails(int inquiryId, String newDetails) {
            Inquiry inquiry = mockInquiries.get(inquiryId);
            if (inquiry == null) {
                throw new RuntimeException("No inquiry found");
            }
            inquiry.setDetails(newDetails);
        }

        @Override
        public void updateInquirySubject(int inquiryId, Integer subjectPersonId) {
            Inquiry inquiry = mockInquiries.get(inquiryId);
            if (inquiry == null) {
                throw new RuntimeException("No inquiry found");
            }

            if (subjectPersonId == null) {
                inquiry.setSubjectPerson(null);
            } else {
                inquiry.setSubjectPerson(new Person(subjectPersonId, "Updated", "Subject", null));
            }
        }

        @Override
        public void deleteInquiry(int inquiryId) {
            if (mockInquiries.remove(inquiryId) == null) {
                throw new RuntimeException("No inquiry found");
            }
        }

        public Inquiry getMockInquiryById(int inquiryId) {
            return mockInquiries.get(inquiryId);
        }
    }

    private static class MockActionLogger extends ActionLogger {
        @Override
        public void log(String actionType, String description) {
        }
    }
}
