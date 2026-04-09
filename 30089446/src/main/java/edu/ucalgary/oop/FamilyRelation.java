package edu.ucalgary.oop;

public class FamilyRelation {
    private int id;
    private Person personOne;
    private String relationshipType;
    private Person personTwo;

    // constructors
    public FamilyRelation(int id, Person personOne, String relationshipType, Person personTwo) {
        setId(id);
        setPersonOne(personOne);
        setRelationshipType(relationshipType);
        setPersonTwo(personTwo);
    }

    public FamilyRelation(Person personOne, String relationshipType, Person personTwo) {
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
        this.personTwo = personTwo;
    }
}