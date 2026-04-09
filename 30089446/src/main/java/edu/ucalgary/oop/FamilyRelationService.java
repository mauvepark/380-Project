package edu.ucalgary.oop;

import java.sql.*;
import java.util.*;

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

  // add a new family relation
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

  // update relationship type
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

    logger.log(
      "UPDATED",
      "family relation " +
        relation.getId() +
        " | Type: " +
        oldRelationshipType +
        " -> " +
        newRelationshipType.trim()
    );
  }

  // delete family relation
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

  // get all family relations
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

  // get family relations for one person
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

  // get one family relation by id
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

  // map result to family relation object
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
