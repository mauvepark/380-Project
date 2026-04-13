package edu.ucalgary.oop;

import java.sql.*;
import java.util.*;

/**
 * Service class for managing family relationships between people in the disaster victim management system.
 * This class provides methods to add, update, delete, and retrieve family relationships.
 * Each family relationship links two people together with a specified relationship type (e.g., "Parent", "Sibling").
 */
public class FamilyRelationService {

  private final FamilyRelationRepository repository;
  private final ActionLogger logger;

  // constructor
  public FamilyRelationService() {
    this.repository = new FamilyRelationRepository(
      DatabaseManager.getInstance().getConnection()
    );
    this.logger = ActionLogger.getInstance();
  }

  public FamilyRelationService(FamilyRelationRepository repository, ActionLogger logger) {
    this.repository = repository;
    this.logger = logger;
  }

  /**
   * Adds a new family relationship between two people with a specified relationship type.
   * The method checks for valid input and prevents creating a relationship between the same person or duplicate relationships.
   * 
   * @param personOne the first person in the relationship
   * @param relationshipType the type of family relationship (e.g., "Parent", "Sibling")
   * @param personTwo the second person in the relationship
   * @return the newly created FamilyRelation object representing the family relationship
   * @throws IllegalArgumentException if the input is invalid (e.g., null values,
   */
  public FamilyRelation addFamilyRelation(
    Person personOne,
    String relationshipType,
    Person personTwo
  ) {
    if (personOne == null) {
      throw new IllegalArgumentException("Person one cannot be null.");
    }
    if (personTwo == null) {
      throw new IllegalArgumentException("Person two cannot be null.");
    }
    if (personOne.getId() <= 0) {
      throw new IllegalArgumentException("Person one must have a valid ID.");
    }
    if (personTwo.getId() <= 0) {
      throw new IllegalArgumentException("Person two must have a valid ID.");
    }
    if (personOne.getId() == personTwo.getId()) {
      throw new IllegalArgumentException(
        "A person cannot have a family relationship with themselves."
      );
    }
    if (relationshipType == null || relationshipType.isBlank()) {
      throw new IllegalArgumentException("Relationship type cannot be blank.");
    }

    if (
      repository.relationExists(
        personOne.getId(),
        personTwo.getId(),
        relationshipType.trim()
      )
    ) {
      throw new IllegalArgumentException(
        "That family relation already exists."
      );
    }

    int relationId = repository.insertFamilyRelation(
      personOne.getId(),
      personTwo.getId(),
      relationshipType.trim()
    );

    FamilyRelation relation = new FamilyRelation(
      relationId,
      personOne,
      relationshipType.trim(),
      personTwo
    );

    logger.log(
      "ADDED",
      "family relation " +
        relationId +
        " | " +
        personOne.getFirstName() +
        " " +
        personOne.getLastName() +
        " - " +
        relationshipType.trim() +
        " - " +
        personTwo.getFirstName() +
        " " +
        personTwo.getLastName()
    );

    return relation;
  }

  /**
   * Updates the type of an existing family relationship.
   *
   * @param relation the family relation to update
   * @param newRelationshipType the new relationship type
   * @throws IllegalArgumentException if the input is invalid (e.g., null values, blank strings)
   */
  public void updateRelationshipType(
    FamilyRelation relation,
    String newRelationshipType
  ) {
    if (relation == null) {
      throw new IllegalArgumentException("Family relation cannot be null.");
    }
    if (newRelationshipType == null || newRelationshipType.isBlank()) {
      throw new IllegalArgumentException("Relationship type cannot be blank.");
    }

    String oldRelationshipType = relation.getRelationshipType();

    repository.updateRelationshipType(
      relation.getId(),
      newRelationshipType.trim()
    );
    relation.setRelationshipType(newRelationshipType.trim());

    logger.log("UPDATED", "family relation " + relation.getId() + " | Type: " + oldRelationshipType + " -> " + newRelationshipType.trim());
  }

  /**
   * Deletes an existing family relationship.
   * 
   * @param relation the family relation to delete
   * @throws IllegalArgumentException if the input is invalid (e.g., null value)
   */
  public void deleteFamilyRelation(FamilyRelation relation) {
    if (relation == null) {
      throw new IllegalArgumentException("Family relation cannot be null.");
    }

    repository.deleteFamilyRelation(relation.getId());

    logger.log(
      "DELETED",
      "family relation " +
        relation.getId() +
        " | " +
        relation.getPersonOne().getFirstName() +
        " " +
        relation.getPersonOne().getLastName() +
        " - " +
        relation.getRelationshipType() +
        " - " +
        relation.getPersonTwo().getFirstName() +
        " " +
        relation.getPersonTwo().getLastName()
    );
  }

  /** 
   * Retrieves all family relationships from the database, including the IDs and names of the two people involved and the type of relationship.
   * 
   * @return a list of FamilyRelation objects representing all family relationships in the database
   * @throws RuntimeException if there is an error retrieving the family relationships from the database.
   */
  public List<FamilyRelation> getAllFamilyRelations() {
    List<FamilyRelation> relations = new ArrayList<>();

    try (ResultSet rs = repository.getAllFamilyRelations()) {
      while (rs.next()) {
        relations.add(mapRowToFamilyRelation(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to load family relations.", e);
    }

    return relations;
  }

  /**
   * Retrieves all family relationships for a specific person from the database, including the IDs and names of the two people involved and the type of relationship.
   * 
   * @param personId the ID of the person for whom to retrieve family relationships
   * @return a list of FamilyRelation objects representing the family relationships for the specified person
   * @throws IllegalArgumentException if the input is invalid (e.g., non-positive person ID)
   * @throws RuntimeException if there is an error retrieving the family relationships from the database
   */
  public List<FamilyRelation> getFamilyRelationsForPerson(int personId) {
    if (personId <= 0) {
      throw new IllegalArgumentException("Person ID must be positive.");
    }

    List<FamilyRelation> relations = new ArrayList<>();

    try (ResultSet rs = repository.getFamilyRelationsForPerson(personId)) {
      while (rs.next()) {
        relations.add(mapRowToFamilyRelation(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException(
        "Failed to load family relations for person.",
        e
      );
    }

    return relations;
  }

  /**
   * Retrieves a family relationship by its ID from the database.
   *
   * @param relationId the ID of the family relation to retrieve
   * @return the FamilyRelation object representing the family relationship, or null if not found
   * @throws IllegalArgumentException if the input is invalid (e.g., non-positive relation ID)
   * @throws RuntimeException if there is an error retrieving the family relationship from the database
   */
  public FamilyRelation getFamilyRelationById(int relationId) {
    if (relationId <= 0) {
      throw new IllegalArgumentException("Relation ID must be positive.");
    }

    try (ResultSet rs = repository.getFamilyRelationById(relationId)) {
      if (rs.next()) {
        return mapRowToFamilyRelation(rs);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to load family relation.", e);
    }

    return null;
  }

  /**
   * Maps a row from the ResultSet to a FamilyRelation object.
   *
   * @param rs the ResultSet containing the row data
   * @return the FamilyRelation object representing the mapped data
   * @throws SQLException if there is an error accessing the ResultSet
   */
  private FamilyRelation mapRowToFamilyRelation(ResultSet rs)
    throws SQLException {
    Person personOne = new Person(
      rs.getInt("person_one_id"),
      rs.getString("person_one_first_name"),
      rs.getString("person_one_last_name"),
      null
    );

    Person personTwo = new Person(
      rs.getInt("person_two_id"),
      rs.getString("person_two_first_name"),
      rs.getString("person_two_last_name"),
      null
    );

    return new FamilyRelation(
      rs.getInt("id"),
      personOne,
      rs.getString("relationship_type"),
      personTwo
    );
  }
}
