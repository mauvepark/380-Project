package edu.ucalgary.oop;

public class Person {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String comments;

    public Person(String firstName, String lastName, String comments) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.comments = comments;
    }

    public Person(int id, String firstName, String lastName, String comments) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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