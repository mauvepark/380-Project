package edu.ucalgary.oop;

public class Person {
    private int id;
    private String firstName;
    private String lastName;
    private String comments;

    public Person(int id, String firstName, String lastName, String comments) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
}