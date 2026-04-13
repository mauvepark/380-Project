package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * The MedicalRecordService class provides methods for managing medical records of disaster victims. 
 * It allows for adding new medical records, updating existing records, deleting records, and retrieving records by ID or by victim. 
 * The service includes validation to ensure that the victim exists and is active, that treatment details are provided, 
 * and that treatment dates are valid. It also logs all actions performed on medical records using the ActionLogger.
 */
public class MedicalRecordService {
    private final MedicalRecordRepository repository;
    private final ActionLogger logger;
    private final VictimRepository victimRepository;
    private final LocationRepository locationRepository;

    /**
     * Constructor for MedicalRecordService. 
     * Initializes the service with a MedicalRecordRepository, ActionLogger, VictimRepository, and LocationRepository.
     */
    public MedicalRecordService() {
        Connection connection = DatabaseManager.getInstance().getConnection();
        this.repository = new MedicalRecordRepository(connection);
        this.logger = ActionLogger.getInstance();
        this.victimRepository = new VictimRepository(connection);
        this.locationRepository = new LocationRepository(connection);
    }

    /**
     * Constructor for MedicalRecordService that allows for dependency injection of the repository, logger, victim repository, and location repository.
     * 
     * @param repository the MedicalRecordRepository to be used for database operations (must not be null)
     * @param logger the ActionLogger to be used for logging actions (must not be null)
     * @param victimRepository the VictimRepository to be used for validating victim existence and status (must not be null)
     * @param locationRepository the LocationRepository to be used for validating location existence (must not be null)
     */
    public MedicalRecordService(MedicalRecordRepository repository, ActionLogger logger, VictimRepository victimRepository, LocationRepository locationRepository) {
        this.repository = repository;
        this.logger = logger;
        this.victimRepository = victimRepository;
        this.locationRepository = locationRepository;
    }

    /**
     * Adds a new medical record for a victim. This method validates the input parameters, checks that the victim exists and is active,
     * and that the location (if provided) exists. It then inserts the medical record into the database and logs the action.
     * 
     * @param victimId the ID of the victim associated with this medical record (must be positive and correspond to an active victim)
     * @param treatmentDetails details about the treatment provided (cannot be null or blank)
     * @param treatmentDate the date when the treatment was provided (cannot be null or in the future)
     * @param locationId the ID of the location where the treatment was provided (can be null if not applicable, but if provided must correspond to an existing location)
     * @return the newly created MedicalRecord object representing the added medical record
     */
    public MedicalRecord addMedicalRecord(int victimId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
        validateVictimId(victimId);
        validateVictimExistsAndIsActive(victimId);
        validateTreatmentDetails(treatmentDetails);
        validateTreatmentDate(treatmentDate);
        validateLocationId(locationId);

        int recordId = repository.insertMedicalRecord(victimId, treatmentDetails, treatmentDate, locationId);
        MedicalRecord record = new MedicalRecord(recordId, victimId, treatmentDetails, treatmentDate, locationId);

        logger.log("ADDED", "medical record " + recordId + " | Victim ID: " + victimId);

        return record;
    }

    /**
     * Updates an existing medical record with new treatment details, date, and location. 
     * This method validates the input parameters, checks that the medical record exists,
     * 
     * @param record the MedicalRecord object to be updated (must not be null and must correspond to an existing medical record)
     * @param newTreatmentDetails the new details about the treatment provided (cannot be null or blank)
     * @param newTreatmentDate the new date when the treatment was provided (cannot be null or in the future)
     * @param newLocationId the new ID of the location where the treatment was provided (can be null if not applicable, but if provided must correspond to an existing location)
     * @throws IllegalArgumentException if the record is null, if the new treatment details are
     */
    public void updateMedicalRecord(MedicalRecord record, String newTreatmentDetails, LocalDate newTreatmentDate, Integer newLocationId) {
        if (record == null) {
            throw new IllegalArgumentException("Medical record cannot be null.");
        }

        validateTreatmentDetails(newTreatmentDetails);
        validateTreatmentDate(newTreatmentDate);
        validateLocationId(newLocationId);

        String oldDetails = record.getTreatmentDetails();
        LocalDate oldDate = record.getTreatmentDate();
        Integer oldLocationId = record.getLocationId();

        repository.updateMedicalRecord(record.getId(), newTreatmentDetails, newTreatmentDate, newLocationId);

        record.setTreatmentDetails(newTreatmentDetails);
        record.setTreatmentDate(newTreatmentDate);
        record.setLocationId(newLocationId);

        logger.log("UPDATED", "medical record " + record.getId() + " | Details: " + oldDetails + " -> " 
                    + newTreatmentDetails + ", Date: " + oldDate + " -> " + newTreatmentDate + ", Location ID: " 
                    + oldLocationId + " -> " + newLocationId);
    }

    /**
     * Deletes a medical record from the database. 
     * This method checks that the record exists before attempting to delete it, and logs the action.
     * 
     * @param record the MedicalRecord object to be deleted (must not be null and must correspond to an existing medical record)
     * @throws IllegalArgumentException if the record is null or does not exist in the database
     */
    public void deleteMedicalRecord(MedicalRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Medical record cannot be null.");
        }

        repository.deleteMedicalRecord(record.getId());

        logger.log("DELETED", "medical record " + record.getId() + " | Victim ID: " + record.getVictimId());
    }

    /**
     * Retrieves a medical record by its ID. 
     * This method checks that the record ID is valid and retrieves the corresponding medical record from the database.
     * 
     * @param recordId the ID of the medical record to retrieve
     * @return the MedicalRecord object if found, null otherwise
     * @throws IllegalArgumentException if the recordId is not positive
     * @throws RuntimeException if there is an error retrieving the medical record from the database
     */
    public MedicalRecord getMedicalRecordById(int recordId) {
        if (recordId <= 0) {
            throw new IllegalArgumentException("Medical record ID must be positive.");
        }

        try (ResultSet rs = repository.getMedicalRecordById(recordId)) {
            if (rs.next()) {
                int locationId = rs.getInt("location_id");
                Integer nullableLocationId = rs.wasNull() ? null : locationId;

                return new MedicalRecord(rs.getInt("id"), rs.getInt("victim_id"), rs.getString("treatment_details"), rs.getDate("treatment_date").toLocalDate(), nullableLocationId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load medical record.", e);
        }

        return null;
    }

    /**
     * Retrieves all medical records for a specific victim from the database, 
     * ordered by treatment date (most recent first) and then by record ID. 
     * 
     * @param victimId the ID of the victim for whom to retrieve medical records
     * @return a list of MedicalRecord objects representing all medical records for the specified victim, ordered by treatment date (most recent first) and then by record ID
     * @throws RuntimeException if there is an error retrieving the medical records from the database
     */
    public List<MedicalRecord> getMedicalRecordsForVictim(int victimId) {
        validateVictimId(victimId);
        validateVictimExistsAndIsActive(victimId);

        List<MedicalRecord> records = new ArrayList<>();

        try (ResultSet rs = repository.getMedicalRecordsForVictim(victimId)) {
            while (rs.next()) {
                int locationId = rs.getInt("location_id");
                Integer nullableLocationId = rs.wasNull() ? null : locationId;

                MedicalRecord record = new MedicalRecord(rs.getInt("id"), rs.getInt("victim_id"), rs.getString("treatment_details"), rs.getDate("treatment_date").toLocalDate(), nullableLocationId);

                records.add(record);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load medical records.", e);
        }

        return records;
    }

    /**
     * Checks if a medical record with the given ID exists in the database.
     * 
     * @param victimId the ID of the victim being checked (must be positive)
     * @throws IllegalArgumentException if the victimId is not positive
     */
    private void validateVictimId(int victimId) {
        if (victimId <= 0) {
            throw new IllegalArgumentException("Victim ID must be positive.");
        }
    }

    /**
     * Validates that a victim with the given ID exists in the database and is active (not soft deleted).
     * 
     * @param victimId the ID of the victim to validate (must be positive)
     * @throws IllegalArgumentException if the victim does not exist or is soft deleted
     */
    private void validateVictimExistsAndIsActive(int victimId) {
        if (!victimRepository.isActiveVictim(victimId)) {
            throw new IllegalArgumentException("Victim does not exist or is soft deleted.");
        }
    }

    /**
     * Validates the treatment details input.
     * 
     * @param treatmentDetails the treatment details to validate (cannot be null or blank)
     * @throws IllegalArgumentException if the treatment details are null or blank
     */
    private void validateTreatmentDetails(String treatmentDetails) {
        if (treatmentDetails == null || treatmentDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment details cannot be null or blank.");
        }
    }

    /**
     * Validates the treatment date input.
     * 
     * @param treatmentDate the treatment date to validate (cannot be null or in the future)
     * @throws IllegalArgumentException if the treatment date is null or in the future
     */
    private void validateTreatmentDate(LocalDate treatmentDate) {
        if (treatmentDate == null) {
            throw new IllegalArgumentException("Treatment date cannot be null.");
        }

        if (treatmentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Treatment date cannot be in the future.");
        }
    }

    /**
     * Validates the location ID input.
     * 
     * @param locationId the location ID to validate (can be null)
     * @throws IllegalArgumentException if the location ID is not null and not positive, or if the location does not exist
     */
    private void validateLocationId(Integer locationId) {
        if (locationId != null && locationId <= 0) {
            throw new IllegalArgumentException("Location ID must be positive.");
        }

        if (locationId != null && !locationRepository.locationExists(locationId)) {
            throw new IllegalArgumentException("Location does not exist.");
        }
    }
}