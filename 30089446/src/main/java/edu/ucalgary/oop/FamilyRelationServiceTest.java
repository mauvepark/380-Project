package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FamilyRelationServiceTest {

    private FamilyRelationService service;
    private MockFamilyRelationRepository mockRepository;
    private MockActionLogger mockLogger;

    @Before
    public void setUp() {
        mockRepository = new MockFamilyRelationRepository();
        mockLogger = new MockActionLogger();
        service = new FamilyRelationService(mockRepository, mockLogger);
    }

    @Test
    public void testAddFamilyRelationValid() {
        Person personOne = new Person(1, "Jane", "Doe", null);
        Person personTwo = new Person(2, "John", "Doe", null);

        FamilyRelation relation = service.addFamilyRelation(personOne, "spouse", personTwo);

        assertNotNull("Family relation should be made", relation);
        assertEquals("Relation ID should be assigned", 1, relation.getId());
        assertEquals("Person one should match", personOne, relation.getPersonOne());
        assertEquals("Person two should match", personTwo, relation.getPersonTwo());
        assertEquals("Relationship type should match", "spouse", relation.getRelationshipType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFamilyRelationNullPersonOneInvalid() {
        service.addFamilyRelation(null, "spouse", new Person(2, "John", "Doe", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFamilyRelationNullPersonTwoInvalid() {
        service.addFamilyRelation(new Person(1, "Jane", "Doe", null), "spouse", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFamilyRelationInvalidPersonOneId() {
        service.addFamilyRelation(new Person(0, "Jane", "Doe", null), "spouse", new Person(2, "John", "Doe", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFamilyRelationInvalidPersonTwoId() {
        service.addFamilyRelation(new Person(1, "Jane", "Doe", null), "spouse", new Person(0, "John", "Doe", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFamilyRelationSamePersonInvalid() {
        Person person = new Person(1, "Jane", "Doe", null);
        service.addFamilyRelation(person, "spouse", person);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddFamilyRelationBlankRelationshipTypeInvalid() {
        service.addFamilyRelation(
            new Person(1, "Jane", "Doe", null),
            "   ",
            new Person(2, "John", "Doe", null)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateFamilyRelationInvalid() {
        Person personOne = new Person(1, "Jane", "Doe", null);
        Person personTwo = new Person(2, "John", "Doe", null);

        service.addFamilyRelation(personOne, "spouse", personTwo);
        service.addFamilyRelation(personOne, "spouse", personTwo);
    }

    @Test
    public void testUpdateRelationshipTypeValid() {
        Person personOne = new Person(1, "Jane", "Doe", null);
        Person personTwo = new Person(2, "John", "Doe", null);

        FamilyRelation relation = service.addFamilyRelation(personOne, "spouse", personTwo);
        service.updateRelationshipType(relation, "parent");

        assertEquals("Relationship type should be updated", "parent", relation.getRelationshipType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateRelationshipTypeNullRelationInvalid() {
        service.updateRelationshipType(null, "parent");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateRelationshipTypeBlankInvalid() {
        Person personOne = new Person(1, "Jane", "Doe", null);
        Person personTwo = new Person(2, "John", "Doe", null);

        FamilyRelation relation = service.addFamilyRelation(personOne, "spouse", personTwo);
        service.updateRelationshipType(relation, "   ");
    }

    @Test
    public void testDeleteFamilyRelationValid() {
        Person personOne = new Person(1, "Jane", "Doe", null);
        Person personTwo = new Person(2, "John", "Doe", null);

        FamilyRelation relation = service.addFamilyRelation(personOne, "spouse", personTwo);
        service.deleteFamilyRelation(relation);

        assertNull("Deleted relation should no longer be stored", mockRepository.getStoredRelationById(relation.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFamilyRelationNullInvalid() {
        service.deleteFamilyRelation(null);
    }

    private static class MockFamilyRelationRepository extends FamilyRelationRepository {
        private final Map<Integer, FamilyRelation> storedRelations = new HashMap<>();
        private int nextId = 1;

        public MockFamilyRelationRepository() {
            super(null);
        }

        @Override
        public int insertFamilyRelation(int personOneId, int personTwoId, String relationshipType) {
            int id = nextId++;

            storedRelations.put(
                id,
                new FamilyRelation(
                    id,
                    new Person(personOneId, "PersonOne", "Test", null),
                    relationshipType,
                    new Person(personTwoId, "PersonTwo", "Test", null)
                )
            );

            return id;
        }

        @Override
        public void updateRelationshipType(int relationId, String newRelationshipType) {
            FamilyRelation relation = storedRelations.get(relationId);
            if (relation == null) {
                throw new RuntimeException("No family relation found");
            }
            relation.setRelationshipType(newRelationshipType);
        }

        @Override
        public void deleteFamilyRelation(int relationId) {
            if (storedRelations.remove(relationId) == null) {
                throw new RuntimeException("No family relation found");
            }
        }

        @Override
        public boolean relationExists(int personOneId, int personTwoId, String relationshipType) {
            for (FamilyRelation relation : storedRelations.values()) {
                if (relation.getPersonOne().getId() == personOneId
                        && relation.getPersonTwo().getId() == personTwoId
                        && relation.getRelationshipType().equals(relationshipType)) {
                    return true;
                }
            }
            return false;
        }

        public FamilyRelation getStoredRelationById(int relationId) {
            return storedRelations.get(relationId);
        }
    }

    private static class MockActionLogger extends ActionLogger {
        @Override
        public void log(String actionType, String description) {
        }
    }
}