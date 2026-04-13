package edu.ucalgary.oop;

import java.time.LocalDateTime;

/**
 * Class to represent an inquiry made by a person (inquirer) about another person (subject) in the disaster victim management system.
 * Each inquiry has a unique ID, the inquirer and subject as Person objects, the date and time of the inquiry, and details about the inquiry.
 */
public class Inquiry {
    private int id;
    private Person inquirer;
    private Person subjectPerson;
    private LocalDateTime inquiryDate;
    private String details;

    // constructors
    public Inquiry(int id, Person inquirer, Person subjectPerson, LocalDateTime inquiryDate, String details) {
        setId(id);
        setInquirer(inquirer);
        setSubjectPerson(subjectPerson);
        setInquiryDate(inquiryDate);
        setDetails(details);
    }

    public Inquiry(Person inquirer, Person subjectPerson, LocalDateTime inquiryDate, String details) {
        setInquirer(inquirer);
        setSubjectPerson(subjectPerson);
        setInquiryDate(inquiryDate);
        setDetails(details);
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Inquiry ID cannot be negative.");
        }
        this.id = id;
    }

    public Person getInquirer() {
        return inquirer;
    }

    public void setInquirer(Person inquirer) {
        if (inquirer == null) {
            throw new IllegalArgumentException("Inquirer cannot be null.");
        }
        this.inquirer = inquirer;
    }

    public Person getSubjectPerson() {
        return subjectPerson;
    }

    public void setSubjectPerson(Person subjectPerson) {
        this.subjectPerson = subjectPerson;
    }

    public LocalDateTime getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(LocalDateTime inquiryDate) {
        if (inquiryDate == null) {
            throw new IllegalArgumentException("Inquiry date cannot be null.");
        }
        if (inquiryDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Inquiry date cannot be in the future.");
        }
        this.inquiryDate = inquiryDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        if (details == null || details.isBlank()) {
            throw new IllegalArgumentException("Inquiry details cannot be blank.");
        }
        this.details = details.trim();
    }
}