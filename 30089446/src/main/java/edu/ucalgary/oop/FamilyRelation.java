package edu.ucalgary.oop;

/**
 * Class to represent a family relationship between two disaster victims. 
 * Each relationship has a type (e.g., "Parent", "Sibling") and involves two people.
 */
public class FamilyRelation {
    private int id;
    private Person personOne;
    private String relationshipType;
    private Person personTwo;

    // constructors
    public FamilyRelation(int id, Person personOne, String relationshipType, Person personTwo) {
        if (personOne == personTwo) {
            throw new IllegalArgumentException("A victim cannot have a family relation with themselves");
        }
        setId(id);
        setPersonOne(personOne);
        setRelationshipType(relationshipType);
        setPersonTwo(personTwo);

    }

    public FamilyRelation(Person personOne, String relationshipType, Person personTwo) {
        if (personOne == personTwo) {
            throw new IllegalArgumentException("A victim cannot have a family relation with themselves");
        }
        setPersonOne(personOne);
        setRelationshipType(relationshipType);
        setPersonTwo(personTwo);
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Relation ID cannot be negative.");
        }
        this.id = id;
    }

    public Person getPersonOne() {
        return personOne;
    }

    public void setPersonOne(Person personOne) {
        if (personOne == null) {
            throw new IllegalArgumentException("Person one cannot be null.");
        }

        if (personOne == personTwo) {
            throw new IllegalArgumentException("A victim cannot have a family relation with themselves");
        }
        this.personOne = personOne;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        if (relationshipType == null || relationshipType.isBlank()) {
            throw new IllegalArgumentException("Relationship type cannot be blank.");
        }
        this.relationshipType = relationshipType.trim();
    }

    public Person getPersonTwo() {
        return personTwo;
    }

    public void setPersonTwo(Person personTwo) {
        if (personTwo == null) {
            throw new IllegalArgumentException("Person two cannot be null.");
        }
        if (personOne == personTwo) {
            throw new IllegalArgumentException("A victim cannot have a family relation with themselves");
        }
        this.personTwo = personTwo;
    }
}