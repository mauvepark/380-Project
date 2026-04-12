package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FamilyRelationTest {

    private FamilyRelation relation;
    private Person personOne;
    private Person personTwo;

    @Before
    public void setUp() {
        personOne = new Person(1, "Harry", "Styles", null);
        personTwo = new Person(2, "Louis", "Tomlinson", null);

        relation = new FamilyRelation(
            1,
            personOne,
            "spouse",
            personTwo
        );
    }

    @Test
    public void testConstructorValid() {
        FamilyRelation testRelation = new FamilyRelation(
            5,
            personOne,
            "siblings",
            personTwo
        );

        assertNotNull("FamilyRelation should be created", testRelation);
        assertEquals("ID should match", 5, testRelation.getId());
        assertEquals("Person one should match", personOne, testRelation.getPersonOne());
        assertEquals("Person two should match", personTwo, testRelation.getPersonTwo());
        assertEquals("Relationship type should match", "siblings", testRelation.getRelationshipType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullPersonOne() {
        new FamilyRelation(
            1,
            null,
            "spouse",
            personTwo
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullPersonTwo() {
        new FamilyRelation(
            1,
            personOne,
            "spouse",
            null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorSamePerson() {
        new FamilyRelation(
            1,
            personOne,
            "spouse",
            personOne
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorBlankRelationshipType() {
        new FamilyRelation(
            1,
            personOne,
            "",
            personTwo
        );
    }

    @Test
    public void testSetId() {
        relation.setId(10);
        assertEquals("ID should update", 10, relation.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIdNegative() {
        relation.setId(-1);
    }

    @Test
    public void testSetPersonOneValid() {
        Person newPerson = new Person(3, "Naruto", "Uzumaki", null);
        relation.setPersonOne(newPerson);

        assertEquals("Person one should update", newPerson, relation.getPersonOne());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPersonOneNull() {
        relation.setPersonOne(null);
    }

    @Test
    public void testSetPersonTwoValid() {
        Person newPerson = new Person(4, "Sasuke", "Uchiha", null);
        relation.setPersonTwo(newPerson);

        assertEquals("Person two should update", newPerson, relation.getPersonTwo());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPersonTwoNull() {
        relation.setPersonTwo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSamePerson() {
        relation.setPersonTwo(personOne);
    }

    @Test
    public void testSettRelationshipType() {
        relation.setRelationshipType("siblings");

        assertEquals("Relationship type should update", "siblings", relation.getRelationshipType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRelationshipTypeBlank() {
        relation.setRelationshipType("");
    }
}