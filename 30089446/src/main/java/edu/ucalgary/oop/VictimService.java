package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;


/**
 * Service class for managing victims.
 * This class handles victim validation, updates, deletion, and loading active victims.
 */
public class VictimService {
    private final VictimRepository repository;
    private final ActionLogger logger;

    /**
     * Constructor for VictimService.
     * Uses the shared database connection and logger.
     */
    public VictimService() {
        this.repository = new VictimRepository(DatabaseManager.getInstance().getConnection());
        this.logger = ActionLogger.getInstance();
    }

    /**
     * Constructor for VictimService with dependency injection.
     *
     * @param repository the victim repository
     * @param logger the action logger
     */
    public VictimService(VictimRepository repository, ActionLogger logger) {
        this.repository = repository;
        this.logger = logger;
    }

    /**
     * Adds a new victim to the system.
     *
     * @param person the person information
     * @param entryDate the entry date
     * @param dateOfBirth the birthdate
     * @param approximateAge the approximate age
     * @param gender the selected gender option
     * @param customGender a custom gender value if needed
     * @param locationId the location ID
     * @return the newly created victim
     * @throws IllegalArgumentException if the victim data is invalid
     * @throws RuntimeException if the victim cannot be added
     */
    public DisasterVictim addVictim(Person person, LocalDate entryDate, LocalDate dateOfBirth, Integer approximateAge, String gender, String customGender, Integer locationId) {
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

        String storedGender = gender;

        if (gender != null && gender.equalsIgnoreCase("please specify")
                && customGender != null && !customGender.isBlank()) {
            storedGender = customGender;
        }

        repository.insertDisasterVictim(personId, dateOfBirth, approximateAge, storedGender, entryDate, locationId);

        DisasterVictim victim;
            if (dateOfBirth != null) {
                victim = new DisasterVictim(personId, person.getFirstName(), person.getLastName(), person.getComments(), entryDate, dateOfBirth);
            } else {
                victim = new DisasterVictim( personId, person.getFirstName(), person.getLastName(), person.getComments(), entryDate, approximateAge);
            }

        if (storedGender != null && !storedGender.isBlank()) {
            victim.loadGenderFromDB(storedGender);
        }

        logger.log("ADDED", "disaster victim " + personId + " | Name: " + person.getFirstName() + " " + person.getLastName());

        return victim;
    }

    /**
     * Updates a victim's name.
     *
     * @param victim the victim to update
     * @param newFirstName the new first name
     * @param newLastName the new last name
     * @throws IllegalArgumentException if the victim is null or the first name is blank
     * @throws RuntimeException if the victim name cannot be updated
     */
    public void updateVictimName(DisasterVictim victim, String newFirstName, String newLastName) {
        if (victim == null) {
            throw new IllegalArgumentException("Victim cannot be null.");
        }
        if (newFirstName == null || newFirstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank.");
        }

        // last name can be blank, but not null
        String oldName = victim.getFirstName() + " " + victim.getLastName();

        repository.updateVictimName(victim.getPersonId(), newFirstName, newLastName);

        victim.setFirstName(newFirstName);
        victim.setLastName(newLastName);

        logger.log("UPDATED", "disaster victim " + victim.getPersonId() + " | Name: " + oldName + " -> " + newFirstName + " " + newLastName);
    }

    /**
     * Updates a victim's age information.
     * The update must use either a birthdate or an approximate age, but not both.
     *
     * @param victim the victim to update
     * @param dateOfBirth the new birthdate
     * @param approximateAge the new approximate age
     * @throws IllegalArgumentException if the input is invalid
     * @throws RuntimeException if the victim age cannot be updated
     */
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

    /**
     * Soft deletes a victim.
     *
     * @param victim the victim to soft delete
     * @throws IllegalArgumentException if the victim is null
     * @throws RuntimeException if the victim cannot be soft deleted
     */
    public void softDeleteVictim(DisasterVictim victim) {
        if (victim == null) {
            throw new IllegalArgumentException("Victim cannot be null.");
        }

        repository.softDeleteVictim(victim.getPersonId());

        logger.log("SOFT DELETED", "disaster victim " + victim.getPersonId() + " | Name: " + victim.getFirstName() + " " + victim.getLastName());
    }

    /**
     * Hard deletes a victim.
     *
     * @param victim the victim to hard delete
     * @throws IllegalArgumentException if the victim is null
     * @throws RuntimeException if the victim cannot be hard deleted
     */
    public void hardDeleteVictim(DisasterVictim victim) {
        if (victim == null) {
            throw new IllegalArgumentException("Victim cannot be null.");
        }

        repository.hardDeleteVictim(victim.getPersonId());

        logger.log("DELETED", "disaster victim " + victim.getPersonId() + " | Name: " + victim.getFirstName() + " " + victim.getLastName());
    }

    /**
     * Gets all active victims from the database.
     *
     * @return a list of active victims
     * @throws RuntimeException if the victims cannot be loaded
     */
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

                DisasterVictim victim;
                    if (dob != null) {
                        victim = new DisasterVictim(personId, firstName, lastName, comments, entryDate, dob);
                    } else {
                        victim = new DisasterVictim(personId, firstName, lastName, comments, entryDate, approximateAge);
                    }

                if (gender != null && !gender.isBlank()) {
                    victim.loadGenderFromDB(gender);
                }

                victims.add(victim);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load victims.", e);
        }

        return victims;
    }
}
