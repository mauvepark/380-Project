package edu.ucalgary.oop;

import java.sql.*;
import java.util.*;
import java.time.LocalDate;


public class VictimService {
    private final VictimRepository repository;
    private final ActionLogger logger;

    // constructor
    public VictimService() {
        this.repository = new VictimRepository(DatabaseManager.getInstance().getConnection());
        this.logger = ActionLogger.getInstance();
    }

    // add a new victim to the database
    public DisasterVictim addVictim(Person person, LocalDate entryDate, LocalDate dateOfBirth, Integer approximateAge, 
           String gender, Integer locationId) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null.");
        }
        if (entryDate == null) {
            throw new IllegalArgumentException("Entry date cannot be null.");
        }
        if ((dateOfBirth == null && approximateAge == null) ||
            (dateOfBirth != null && approximateAge != null)) {
            throw new IllegalArgumentException("Provide either birthdate or approximate age, not both.");
        }

        // insert person to get ID
        int personId = repository.insertPerson(person.getFirstName(), person.getLastName(), person.getComments());

        repository.insertDisasterVictim(personId,dateOfBirth,approximateAge, gender, entryDate, locationId);

        Person savedPerson = new Person(personId, person.getFirstName(),person.getLastName(), person.getComments()
        );

        DisasterVictim victim;
        if (dateOfBirth != null) {
            victim = new DisasterVictim(savedPerson, entryDate, dateOfBirth);
        } else {
            victim = new DisasterVictim(savedPerson, entryDate, approximateAge);
        }

        if (gender != null && !gender.isBlank()) {
            victim.setGender(gender);
        }

        logger.log("ADDED", "disaster victim " + personId + " | Name: " + person.getFirstName() + " " + person.getLastName());

        return victim;
    }

    // update victim name
    public void updateVictimName(DisasterVictim victim, String newFirstName, String newLastName) {
        if (victim == null) {
            throw new IllegalArgumentException("Victim cannot be null.");
        }
        if (newFirstName == null || newFirstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank.");
        }

        // last name can be blank, but not null
        String oldName = victim.getPerson().getFirstName() + " " + victim.getPerson().getLastName();

        repository.updateVictimName(victim.getPersonId(), newFirstName, newLastName);

        victim.getPerson().setFirstName(newFirstName);
        victim.getPerson().setLastName(newLastName);

        logger.log("UPDATED", "disaster victim " + victim.getPersonId() + " | Name: " + oldName + " -> " + newFirstName + " " + newLastName);
    }

    // update victim info 
    public void updateVictimAgeInfo(DisasterVictim victim, LocalDate dateOfBirth, Integer approximateAge) {
        if (victim == null) {
            throw new IllegalArgumentException("Victim cannot be null.");
        }
        if ((dateOfBirth == null && approximateAge == null) ||
            (dateOfBirth != null && approximateAge != null)) {
            throw new IllegalArgumentException("Provide either birthdate or approximate age, but not both.");
        }
        if (victim.getDateOfBirth() != null && approximateAge != null) {
            throw new IllegalArgumentException("Cannot replace birthdate with approximate age.");
        }

        repository.updateVictimAgeInfo(victim.getPersonId(), dateOfBirth, approximateAge);

        if (dateOfBirth != null) {
            victim.setDateOfBirth(dateOfBirth);
            logger.log("UPDATED", "disaster victim " + victim.getPersonId() + " | Approximate age updated to birthdate " + dateOfBirth);
        } else {
            victim.setApproximateAge(approximateAge);
            logger.log("UPDATED", "disaster victim " + victim.getPersonId() + " | Approximate age updated to " + approximateAge);
        }
    }

    // soft delete
    public void softDeleteVictim(DisasterVictim victim) {
        if (victim == null) {
            throw new IllegalArgumentException("Victim cannot be null.");
        }

        repository.softDeleteVictim(victim.getPersonId());

        logger.log("SOFT DELETED", "disaster victim " + victim.getPersonId() + " | Name: " + victim.getPerson().getFirstName() + " " + victim.getPerson().getLastName());
    }

    // hard delete
    public void hardDeleteVictim(DisasterVictim victim) {
        if (victim == null) {
            throw new IllegalArgumentException("Victim cannot be null.");
        }

        repository.hardDeleteVictim(victim.getPersonId());

        logger.log("DELETED", "disaster victim " + victim.getPersonId() + " | Name: " + victim.getPerson().getFirstName() + " " + victim.getPerson().getLastName());
    }

    // get all active victims from the database
    public List<DisasterVictim> getActiveVictims() {
        List<DisasterVictim> victims = new ArrayList<>();

        try (ResultSet rs = repository.getActiveVictims()) {
            while (rs.next()) {
                int personId = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String comments = rs.getString("comments");
                LocalDate dob = rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null;
                int approxAgeValue = rs.getInt("approximate_age");
                Integer approximateAge = rs.wasNull() ? null : approxAgeValue;
                String gender = rs.getString("gender");
                LocalDate entryDate = rs.getDate("entry_date") != null ? rs.getDate("entry_date").toLocalDate() : null;

                Person person = new Person(personId, firstName, lastName, comments);

                DisasterVictim victim;
                if (dob != null) {
                    victim = new DisasterVictim(person, entryDate, dob);
                } else {
                    victim = new DisasterVictim(person, entryDate, approximateAge);
                }

                if (gender != null && !gender.isBlank()) {
                    victim.setGender(gender);
                }

                victims.add(victim);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load victims.", e);
        }

        return victims;
    }
}