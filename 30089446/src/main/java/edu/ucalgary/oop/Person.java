package edu.ucalgary.oop;

/**
 * Represents a person in the disaster victim management system.
 * Stores the person's identifier, name, and any related comments.
 */
public class Person {

  protected int id;
  protected String firstName;
  protected String lastName;
  protected String comments;

  /**
   * Creates a person without a database identifier.
   *
   * @param firstName the person's first name
   * @param lastName the person's last name
   * @param comments additional comments associated with the person
   */
  public Person(String firstName, String lastName, String comments) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.comments = comments;
  }

  /**
   * Creates a person with a known identifier.
   *
   * @param id the person's unique identifier
   * @param firstName the person's first name
   * @param lastName the person's last name
   * @param comments additional comments associated with the person
   */
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
