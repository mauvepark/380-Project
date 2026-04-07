/*
Copyright Ann Barcomb and Khawla Shnaikat, 2024-2025
Licensed under GPL v3
See LICENSE.txt for more information.
*/

package edu.ucalgary.oop;

public class FamilyRelation {
    private DisasterVictim personOne;
    private String relationshipTo;
    private DisasterVictim personTwo;

    public FamilyRelation(DisasterVictim personOne, String relationshipTo, DisasterVictim personTwo) 
            throws IllegalArgumentException {
        if (personOne == null || personTwo == null) {
            throw new IllegalArgumentException("Persons in a family relation cannot be null");
        }
        this.personOne = personOne;
        this.relationshipTo = relationshipTo;
        this.personTwo = personTwo;
    }

    public DisasterVictim getPersonOne() {
        return personOne;
    }

    public void setPersonOne(DisasterVictim personOne) throws IllegalArgumentException {
        if (personOne == null) {
            throw new IllegalArgumentException("PersonOne cannot be null");
        }
        this.personOne = personOne;
    }

    public String getRelationshipTo() {
        return relationshipTo;
    }

    public void setRelationshipTo(String relationshipTo) {
        this.relationshipTo = relationshipTo;
    }

    public DisasterVictim getPersonTwo() {
        return personTwo;
    }

    public void setPersonTwo(DisasterVictim personTwo) throws IllegalArgumentException {
        if (personTwo == null) {
            throw new IllegalArgumentException("PersonTwo cannot be null");
        }
        this.personTwo = personTwo;
    }
}
