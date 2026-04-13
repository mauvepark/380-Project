package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the MedicalRecordService class. 
 * These tests cover adding, updating, and deleting medical records, as well as validation of inputs and retrieval of medical records for a victim. 
 * Mock repositories and a mock logger are used to isolate the service logic from external dependencies. 
 * Each test checks that the expected behavior occurs and that appropriate exceptions are thrown for invalid inputs.
 */
public class MedicalRecordServiceTest {

    private MedicalRecordService service;
    private MockMedicalRecordRepository mockRepository;
    private MockVictimRepository mockVictimRepository;
    private MockLocationRepository mockLocationRepository;
    private MockActionLogger mockLogger;
    
    @Before
    public void setUp() {
        mockRepository = new MockMedicalRecordRepository();
        mockVictimRepository = new MockVictimRepository();
        mockLocationRepository = new MockLocationRepository();
        mockLogger = new MockActionLogger();

        service = new MedicalRecordService(
            mockRepository,
            mockLogger,
            mockVictimRepository,
            mockLocationRepository
        );
    }

    @Test
    public void testAddMedicalRecordValid() {
        MedicalRecord record = service.addMedicalRecord(
            1,
            "Broken arm",
            LocalDate.parse("2026-01-01"),
            10
        );

        assertNotNull("Medical record should be made", record);
        assertEquals("Victim ID should match", 1, record.getVictimId());
        assertEquals("Treatment details should match", "Broken arm", record.getTreatmentDetails());
        assertEquals("Treatment date should match", LocalDate.parse("2026-01-01"), record.getTreatmentDate());
        assertEquals("Location ID should match", Integer.valueOf(10), record.getLocationId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMedicalRecordInvalidVictim() {
        service.addMedicalRecord(
            999,
            "Treatment",
            LocalDate.parse("2026-01-01"),
            10
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMedicalRecordBlankDetails() {
        service.addMedicalRecord(
            1,
            "   ",
            LocalDate.parse("2026-01-01"),
            10
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMedicalRecordFutureDate() {
        service.addMedicalRecord(
            1,
            "Broken arm",
            LocalDate.now().plusDays(1),
            10
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMedicalRecordInvalidLocation() {
        service.addMedicalRecord(
            1,
            "Broken arm",
            LocalDate.parse("2026-01-01"),
            999
        );
    }

    @Test
    public void testUpdateMedicalRecordValid() {
        MedicalRecord record = service.addMedicalRecord(
            1,
            "Old",
            LocalDate.parse("2026-01-01"),
            10
        );

        service.updateMedicalRecord(
            record,
            "Updated",
            LocalDate.parse("2026-01-01"),
            11
        );

        assertEquals("Treatment details should be updated", "Updated", record.getTreatmentDetails());
        assertEquals("Treatment date should be updated", LocalDate.parse("2026-01-01"), record.getTreatmentDate());
        assertEquals("Location ID should be updated", Integer.valueOf(11), record.getLocationId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateMedicalRecordNullRecord() {
        service.updateMedicalRecord(
            null,
            "Update",
            LocalDate.parse("2026-01-01"),
            10
        );
    }

    @Test
    public void testDeleteMedicalRecordValid() {
        MedicalRecord record = service.addMedicalRecord(
            1,
            "Treatment",
            LocalDate.parse("2026-01-01"),
            10
        );

        service.deleteMedicalRecord(record);

        assertEquals("DB should be empty after delete", 0, mockRepository.records.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteMedicalRecordNullRecord() {
        service.deleteMedicalRecord(null);
    }

    private static class MockMedicalRecordRepository extends MedicalRecordRepository {
        private final List<MedicalRecord> records = new ArrayList<>();
        private int nextId = 1;

        public MockMedicalRecordRepository() {
            super(null);
        }

        @Override
        public int insertMedicalRecord(int victimId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
            int id = nextId++;
            records.add(new MedicalRecord(id, victimId, treatmentDetails, treatmentDate, locationId));
            return id;
        }

        @Override
        public void updateMedicalRecord(int recordId, String treatmentDetails, LocalDate treatmentDate, Integer locationId) {
            for (MedicalRecord record : records) {
                if (record.getId() == recordId) {
                    record.setTreatmentDetails(treatmentDetails);
                    record.setTreatmentDate(treatmentDate);
                    record.setLocationId(locationId);
                    return;
                }
            }
            throw new RuntimeException("No medical record found");
        }

        @Override
        public void deleteMedicalRecord(int recordId) {
            boolean removed = records.removeIf(record -> record.getId() == recordId);
            if (!removed) {
                throw new RuntimeException("No medical record found");
            }
        }
    }

    private static class MockVictimRepository extends VictimRepository {
        public MockVictimRepository() {
            super(null);
        }

        @Override
        public boolean isActiveVictim(int personId) {
            return personId == 1;
        }
    }

    private static class MockLocationRepository extends LocationRepository {
        public MockLocationRepository() {
            super(null);
        }

        @Override
        public boolean locationExists(int locationId) {
            return locationId == 10 || locationId == 11;
        }
    }

    private static class MockActionLogger extends ActionLogger {
        @Override
        public void log(String actionType, String description) {
        }
    }
}