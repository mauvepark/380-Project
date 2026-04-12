package edu.ucalgary.oop;

import org.junit.Test;

import static org.junit.Assert.*;

public class PersonTest {

    @Test
    public void testConstructorWithValidData() {
        Person person = new Person(1, "John", "Smith", "Test");

        assertNotNull("Constructor should create a Person object", person);
        assertEquals("ID should be set correctly", 1, person.getId());
        assertEquals("First name should be set correctly", "John", person.getFirstName());
        assertEquals("Last name should be set correctly", "Smith", person.getLastName());
        assertEquals("Comments should be set correctly", "Test", person.getComments());
    }

    @Test
    public void testConstructorWithNoID() {
        Person person = new Person("Jane", "Doe", "Test");

        assertNotNull("Constructor should create a Person object", person);
        assertEquals("First name should be set correctly", "Jane", person.getFirstName());
        assertEquals("Last name should be set correctly", "Doe", person.getLastName());
        assertEquals("Comments should be set correctly", "Test", person.getComments());
    }

    @Test
    public void testSetId() {
        Person person = new Person("Apple", "Bees", "Test");
        person.setId(15);

        assertEquals("setId should update the id", 15, person.getId());
    }

    @Test
    public void testSetFirstName() {
        Person person = new Person("Taco", "Bell", "Test");
        person.setFirstName("Church");

        assertEquals("setFirstName should update first name", "Church", person.getFirstName());
    }

    @Test
    public void testSetLastName() {
        Person person = new Person("Mc", "Donalds", "Test");
        person.setLastName("Gee");

        assertEquals("setLastName should update last name", "Gee", person.getLastName());
    }

    @Test
    public void testSetComments() {
        Person person = new Person("Noor", "Ali", "Test");
        person.setComments("100% on this godforsaken project");

        assertEquals("setComments should update comments", "100% on this godforsaken project", person.getComments());
    }

    @Test
    public void testSetLastNameToNull() {
        Person person = new Person("Meow", "Meow", "Test");
        person.setLastName(null);

        assertNull("Last name should become null", person.getLastName());
    }

    @Test
    public void testSetCommentsToNull() {
        Person person = new Person("Meow", "Meow", "Test");
        person.setComments(null);

        assertNull("Comments should become null", person.getComments());
    }
}