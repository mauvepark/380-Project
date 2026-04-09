package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class MedicalRecordService {
    private final MedicalRecordRepository repository;
    private final ActionLogger logger;
    private final VictimRepository victimRepository;
    private final LocationRepository locationRepository;

    // constructor
    public MedicalRecordService() {
        Connection connection = DatabaseManager.getInstance().getConnection();
        this.repository = new MedicalRecordRepository(connection);
        this.logger = ActionLogger.getInstance();
        this.victimRepository = new VictimRepository(connection);
        this.locationRepository = new LocationRepository(connection);
    }

    // add a new medical record
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

    // update medical record
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

    // delete medical record
    public void deleteMedicalRecord(MedicalRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Medical record cannot be null.");
        }

        repository.deleteMedicalRecord(record.getId());

        logger.log("DELETED", "medical record " + record.getId() + " | Victim ID: " + record.getVictimId());
    }

    // get one medical record by id
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

    // get all medical records for one victim
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

    // validation
    private void validateVictimId(int victimId) {
        if (victimId <= 0) {
            throw new IllegalArgumentException("Victim ID must be positive.");
        }
    }

    private void validateVictimExistsAndIsActive(int victimId) {
        if (!victimRepository.isActiveVictim(victimId)) {
            throw new IllegalArgumentException("Victim does not exist or is soft deleted.");
        }
    }

    private void validateTreatmentDetails(String treatmentDetails) {
        if (treatmentDetails == null || treatmentDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment details cannot be null or blank.");
        }
    }

    private void validateTreatmentDate(LocalDate treatmentDate) {
        if (treatmentDate == null) {
            throw new IllegalArgumentException("Treatment date cannot be null.");
        }

        if (treatmentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Treatment date cannot be in the future.");
        }
    }

    private void validateLocationId(Integer locationId) {
        if (locationId != null && locationId <= 0) {
            throw new IllegalArgumentException("Location ID must be positive.");
        }

        if (locationId != null && !locationRepository.locationExists(locationId)) {
            throw new IllegalArgumentException("Location does not exist.");
        }
    }
}