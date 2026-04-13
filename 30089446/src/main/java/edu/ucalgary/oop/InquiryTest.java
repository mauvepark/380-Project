package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit tests for the Inquiry class.
 * These tests check object creation and validation for inquiries.
 */
public class InquiryTest {

    private Inquiry inquiry;
    private Person inquirer;
    private Person subjectPerson;

    @Before
    public void setUp() {
        inquirer = new Person(1, "Justin", "Bieber", null);
        subjectPerson = new Person(2, "Noor", "Ali", null);

        inquiry = new Inquiry(
            1,
            inquirer,
            subjectPerson,
            LocalDateTime.now().minusDays(1),
            "details"
        );
    }

    @Test
    public void testConstructorValid() {
        LocalDateTime inquiryDate = LocalDateTime.now().minusHours(2);

        Inquiry testInquiry = new Inquiry(
            5,
            inquirer,
            subjectPerson,
            inquiryDate,
            "details"
        );

        assertNotNull("Inquiry should be created", testInquiry);
        assertEquals("ID should match", 5, testInquiry.getId());
        assertEquals("Inquirer should match", inquirer, testInquiry.getInquirer());
        assertEquals("Subject person should match", subjectPerson, testInquiry.getSubjectPerson());
        assertEquals("Inquiry date should match", inquiryDate, testInquiry.getInquiryDate());
        assertEquals("Details should match", "details", testInquiry.getDetails());
    }

    @Test
    public void testConstructorNullSubjectPerson() {
        Inquiry testInquiry = new Inquiry(
            2,
            inquirer,
            null,
            LocalDateTime.now().minusMinutes(30),
            "details"
        );

        assertNotNull("Inquiry should be created with null subject", testInquiry);
        assertNull("Subject person should be allowed to be null", testInquiry.getSubjectPerson());
        assertEquals("Details should still match", "details", testInquiry.getDetails());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullInquirer() {
        new Inquiry(
            1,
            null,
            subjectPerson,
            LocalDateTime.now().minusDays(1),
            "details"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullInquiryDate() {
        new Inquiry(
            1,
            inquirer,
            subjectPerson,
            null,
            "details"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFutureInquiryDate() {
        new Inquiry(
            1,
            inquirer,
            subjectPerson,
            LocalDateTime.now().plusDays(1),
            "details"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBlankDetails() {
        new Inquiry(
            1,
            inquirer,
            subjectPerson,
            LocalDateTime.now().minusDays(1),
            "   "
        );
    }

    @Test
    public void testSetAndGetId() {
        inquiry.setId(10);
        assertEquals("ID should update", 10, inquiry.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIdNegativeInvalid() {
        inquiry.setId(-1);
    }

    @Test
    public void testSetInquirerValid() {
        Person newInquirer = new Person(3, "Noor", "Ali", null);
        inquiry.setInquirer(newInquirer);

        assertEquals("Inquirer should update", newInquirer, inquiry.getInquirer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInquirerNullInvalid() {
        inquiry.setInquirer(null);
    }

    @Test
    public void testSetSubjectPersonValid() {
        Person newSubject = new Person(4, "Justin", "Bieber", null);
        inquiry.setSubjectPerson(newSubject);

        assertEquals("Subject person should update", newSubject, inquiry.getSubjectPerson());
    }

    @Test
    public void testSetSubjectPersonNull() {
        inquiry.setSubjectPerson(null);

        assertNull("Subject person should be allowed to become null", inquiry.getSubjectPerson());
    }

    @Test
    public void testSetInquiryDate() {
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        inquiry.setInquiryDate(newDate);

        assertEquals("Inquiry date should update", newDate, inquiry.getInquiryDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInquiryDateNullInvalid() {
        inquiry.setInquiryDate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInquiryDateFutureInvalid() {
        inquiry.setInquiryDate(LocalDateTime.now().plusHours(1));
    }

    @Test
    public void testSetAndGetDetails() {
        inquiry.setDetails("update");

        assertEquals("Details should update", "update", inquiry.getDetails());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDetailsBlank() {
        inquiry.setDetails("   ");
    }
}
