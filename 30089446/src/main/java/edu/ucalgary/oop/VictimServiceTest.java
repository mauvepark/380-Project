package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the VictimService class.
 * These tests check victim creation, updates, deletion, and loading active victims.
 */
public class VictimServiceTest {

    private VictimService service;
    private MockVictimRepository mockRepository;
    private MockActionLogger mockLogger;

    @Before
    public void setUp() {
        mockRepository = new MockVictimRepository();
        mockLogger = new MockActionLogger();
        service = new VictimService(mockRepository, mockLogger);
    }

    @Test
    public void testAddVictimWithBirthdateValid() {
        Person person = new Person(0, "Naruto", "Uzumaki", "Test");

        DisasterVictim victim = service.addVictim(
            person,
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2000-01-01"),
            null,
            "Man",
            null,
            null
        );

        assertNotNull("Victim should be created", victim);
        assertEquals("First name should match", "Naruto", victim.getFirstName());
        assertEquals("Last name should match", "Uzumaki", victim.getLastName());
        assertEquals("Birthdate should match", LocalDate.parse("2000-01-01"), victim.getDateOfBirth());
        assertNull("Approximate age should be null when birthdate is used", victim.getApproximateAge());
        assertEquals("Gender should match", "Man", victim.getGender());
    }

    @Test
    public void testAddVictimWithApproximateAgeValid() {
        Person person = new Person(0, "Sasuke", "Uchiha", "test");

        DisasterVictim victim = service.addVictim(
            person,
            LocalDate.parse("2026-01-01"),
            null,
            30,
            "Please specify",
            "Non-binary",
            null
        );

        assertNotNull("Victim should be created", victim);
        assertEquals("Approximate age should match", Integer.valueOf(30), victim.getApproximateAge());
        assertNull("Birthdate should be null when approximate age is used", victim.getDateOfBirth());
        assertEquals("Custom gender should be stored", "Non-binary", victim.getGender());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddVictimNullPerson() {
        service.addVictim(
            null,
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2000-01-01"),
            null,
            "Man",
            null,
            null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddVictimNullEntryDate() {
        service.addVictim(
            new Person(0, "Sakura", "Haruno", null),
            null,
            LocalDate.parse("2000-01-01"),
            null,
            "Woman",
            null,
            null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddVictimWithBothBirthdateAndApproximateAgeInvalid() {
        service.addVictim(
            new Person(0, "Shikamaru", "Nara", null),
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2000-01-01"),
            20,
            "Man",
            null,
            null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddVictimWithNeitherBirthdateNorApproximateAgeInvalid() {
        service.addVictim(
            new Person(0, "Ino", "Yamanaka", null),
            LocalDate.parse("2026-01-01"),
            null,
            null,
            "Woman",
            null,
            null
        );
    }

    @Test
    public void testUpdateVictimNameValid() {
        DisasterVictim victim = new DisasterVictim(
            1,
            "Hiruzen",
            "Sarutobi",
            null,
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2000-01-01")
        );

        service.updateVictimName(victim, "Tsunade", "Senju");

        assertEquals("First name should be updated", "Tsunade", victim.getFirstName());
        assertEquals("Last name should be updated", "Senju", victim.getLastName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateVictimNameBlankFirstNameInvalid() {
        DisasterVictim victim = new DisasterVictim(
            1,
            "Minato",
            "Namikaze",
            null,
            LocalDate.parse("2026-01-01"),
            LocalDate.parse("2000-01-01")
        );

        service.updateVictimName(victim, "   ", "Namikaze");
    }

    @Test
    public void testUpdateVictimAgeApproximateAgeToBirthdateValid() {
        DisasterVictim victim = new DisasterVictim(
            1,
            "Kushina",
            "Uzumaki",
            null,
            LocalDate.parse("2026-01-01"),
            30
        );

        service.updateVictimAgeInfo(victim, LocalDate.parse("2000-01-01"), null);

        assertEquals("Birthdate should be updated", LocalDate.parse("2000-01-01"), victim.getDateOfBirth());
        assertNull("Approximate age should be cleared after setting birthdate", victim.getApproximateAge());
    }

    @Test
    public void testSoftDeleteVictimValid() {
        DisasterVictim victim = new DisasterVictim(
            1,
            "Choji",
            "Akimichi",
            null,
            LocalDate.parse("2026-01-01"),
            30
        );

        service.softDeleteVictim(victim);

        assertTrue("Victim should be marked soft deleted in mock repository",
            mockRepository.softDeletedVictims.containsKey(1));
    }

    @Test
    public void testHardDeleteVictimValid() {
        DisasterVictim victim = new DisasterVictim(
            1,
            "Kakashi",
            "Hatake",
            null,
            LocalDate.parse("2026-01-01"),
            30
        );

        service.hardDeleteVictim(victim);

        assertTrue("Victim should be marked hard deleted in mock repository",
            mockRepository.hardDeletedVictims.containsKey(1));
    }

    private static class MockVictimRepository extends VictimRepository {
        private int nextPersonId = 1;
        private final Map<Integer, Boolean> softDeletedVictims = new HashMap<>();
        private final Map<Integer, Boolean> hardDeletedVictims = new HashMap<>();

        public MockVictimRepository() {
            super(null);
        }

        @Override
        public int insertPerson(String firstName, String lastName, String comments) {
            return nextPersonId++;
        }

        @Override
        public void insertDisasterVictim(int personId, LocalDate dateOfBirth, Integer approximateAge,
                                         String gender, LocalDate entryDate, Integer locationId) {
            // do nothing for tests
        }

        @Override
        public void updateVictimName(int personId, String newFirstName, String newLastName) {
            // do nothing for tests
        }

        @Override
        public void updateVictimAgeInfo(int personId, LocalDate dateOfBirth, Integer approximateAge) {
            // do nothing for tests
        }

        @Override
        public void softDeleteVictim(int personId) {
            softDeletedVictims.put(personId, true);
        }

        @Override
        public void hardDeleteVictim(int personId) {
            hardDeletedVictims.put(personId, true);
        }
    }

    private static class MockActionLogger extends ActionLogger {
        @Override
        public void log(String actionType, String description) {
            // do nothing for tests
        }
    }
}
