package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Repository class for managing skills and victim skill assignments in the disaster victim management system.
 * Provides methods to retrieve, insert, delete, and search skill-related records in the database.
 * Each method interacts with the database using SQL queries and handles any SQL exceptions that may occur.
 */
public class SkillRepository {
    private final Connection dbConnect;

    /**
     * Constructor for SkillRepository. Initializes the repository with a database connection.
     * 
     * @param dbConnect the database connection used to perform skill-related queries
     */
    public SkillRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    /**
     * Retrieves all skills from the database, ordered by category and skill name.
     * 
     * @return a list of all skills stored in the database
     * @throws RuntimeException if there is an error retrieving the skills from the database
     */
    public List<Skill> getAllSkills() {
        String query = "SELECT id, skill_name, category FROM Skill ORDER BY category, skill_name";

        List<Skill> skills = new ArrayList<>();

        try (PreparedStatement stmt = dbConnect.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Skill skill = new Skill(rs.getInt("id"), rs.getString("skill_name"), rs.getString("category"));
                skills.add(skill);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load skills.", e);
        }

        return skills;
    }

    /**
     * Retrieves a skill from the database based on its ID.
     * 
     * @param skillId the ID of the skill to retrieve
     * @return the matching Skill if found, otherwise null
     * @throws RuntimeException if there is an error retrieving the skill from the database
     */
    public Skill getSkillById(int skillId) {
        String query = "SELECT id, skill_name, category FROM Skill WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, skillId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Skill(rs.getInt("id"), rs.getString("skill_name"), rs.getString("category"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get skill.", e);
        }

        return null;
    }

    /**
     * Retrieves a skill from the database based on its name and category.
     * 
     * @param skillName the name of the skill to retrieve
     * @param category the category of the skill to retrieve
     * @return the matching Skill if found, otherwise null
     * @throws RuntimeException if there is an error retrieving the skill from the database
     */
    public Skill getSkillByNameAndCategory(String skillName, String category) {
        String query = "SELECT id, skill_name, category FROM Skill WHERE skill_name = ? AND category = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, skillName);
            stmt.setString(2, category);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Skill(rs.getInt("id"), rs.getString("skill_name"), rs.getString("category")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get skill.", e);
        }

        return null;
    }

    /**
     * Inserts a new skill into the database.
     * 
     * @param skillName the name of the skill to insert
     * @param category the category of the skill to insert
     * @return the ID of the newly inserted skill
     * @throws RuntimeException if there is an error inserting the skill into the database
     */
    public int insertSkill(String skillName, String category) {
        String query = "INSERT INTO Skill (skill_name, category) VALUES (?, ?) RETURNING id";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, skillName);
            stmt.setString(2, category);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert skill.", e);
        }

        throw new RuntimeException("Could not insert skill.");
    }

    /**
     * Inserts a new victim skill record into the database.
     * This record links a victim to a skill and may include optional details such as
     * language capabilities, certification expiry, and proficiency level.
     * 
     * @param victimId the ID of the victim receiving the skill
     * @param skillId the ID of the skill being assigned
     * @param details additional details about the skill assignment
     * @param languageCapabilities the language capabilities associated with the skill, if applicable
     * @param certificationExpiry the certification expiry date for the skill, if applicable
     * @param proficiencyLevel the proficiency level associated with the skill
     * @return the ID of the newly inserted victim skill record
     * @throws RuntimeException if there is an error inserting the victim skill into the database
     */
    public int insertVictimSkill(int victimId, int skillId, String details, String languageCapabilities, 
                                 LocalDate certificationExpiry, String proficiencyLevel) {
        String query = """
                            INSERT INTO VictimSkill
                            (victim_id, skill_id, details, language_capabilities, certification_expiry, proficiency_level)
                            VALUES (?, ?, ?, ?, ?, ?)
                            RETURNING id
                        """;

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.setInt(2, skillId);
            stmt.setString(3, details);
            stmt.setString(4, languageCapabilities);

            if (certificationExpiry == null) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, java.sql.Date.valueOf(certificationExpiry));
            }

            stmt.setString(6, proficiencyLevel);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert victim skill.", e);
        }

        throw new RuntimeException("Could not insert victim skill.");
    }

    /**
     * Deletes a victim skill record from the database based on its ID.
     * 
     * @param victimSkillId the ID of the victim skill record to delete
     * @throws RuntimeException if there is an error deleting the victim skill or if no matching record is found
     */
    public void deleteVictimSkill(int victimSkillId) {
        String query = "DELETE FROM VictimSkill WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimSkillId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No victim skill found with id " + victimSkillId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete victim skill.", e);
        }
    }

    /**
     * Deletes all victim skill records associated with a specific victim.
     * This method is intended for use during hard deletion of a victim.
     * 
     * @param victimId the ID of the victim whose skill records should be deleted
     * @throws RuntimeException if there is an error deleting the victim skill records from the database
     */
    public void deleteAllSkillsForVictim(int victimId) {
        String query = "DELETE FROM VictimSkill WHERE victim_id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete victim skills for victim.", e);
        }
    }

    /**
     * Checks whether a victim already has a specific skill assigned.
     * 
     * @param victimId the ID of the victim to check
     * @param skillId the ID of the skill to check
     * @return true if the victim has the specified skill, false otherwise
     * @throws RuntimeException if there is an error checking the victim skill in the database
     */
    public boolean victimHasSkill(int victimId, int skillId) {
        String query = "SELECT 1 FROM VictimSkill WHERE victim_id = ? AND skill_id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.setInt(2, skillId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check victim skill.", e);
        }
    }

    /**
     * Retrieves all skill assignments for a specific victim from the database.
     * The results are ordered by victim skill record ID.
     * 
     * @param victimId the ID of the victim whose skills are to be retrieved
     * @return a list of victim skill records associated with the specified victim
     * @throws RuntimeException if there is an error retrieving the victim skills from the database
     */
    public List<VictimSkill> getSkillsForVictim(int victimId) {
        String query = """
                            SELECT id, victim_id, skill_id, details, language_capabilities, certification_expiry, proficiency_level
                            FROM VictimSkill
                            WHERE victim_id = ?
                            ORDER BY id
                      """;

        List<VictimSkill> victimSkills = new ArrayList<>();

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date expiryDate = rs.getDate("certification_expiry");

                    VictimSkill victimSkill = new VictimSkill(
                        rs.getInt("id"),
                        rs.getInt("victim_id"),
                        rs.getInt("skill_id"),
                        rs.getString("details"),
                        rs.getString("language_capabilities"),
                        expiryDate == null ? null : expiryDate.toLocalDate(), 
                        rs.getString("proficiency_level")
                    );

                    victimSkills.add(victimSkill);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get victim skills.", e);
        }

        return victimSkills;
    }

    /**
     * Retrieves all skills in a specific category from the database.
     * The results are ordered by skill name.
     * 
     * @param category the category of skills to retrieve
     * @return a list of skills in the specified category
     * @throws RuntimeException if there is an error retrieving the skills from the database
     */
    public List<Skill> getSkillsByCategory(String category) {
        String query = "SELECT id, skill_name, category FROM Skill WHERE category = ? ORDER BY skill_name";

        List<Skill> skills = new ArrayList<>();

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Skill skill = new Skill(rs.getInt("id"), rs.getString("skill_name"), rs.getString("category"));
                    skills.add(skill);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not get skills by category.", e);
        }

        return skills;
    }

    /**
     * Searches for active victim IDs that have at least one skill in the specified category.
     * Only victims that are not soft-deleted are included in the results.
     * 
     * @param category the skill category to search for
     * @return a list of victim IDs that match the specified skill category
     * @throws RuntimeException if there is an error searching for victims by skill category
     */
    public List<Integer> searchVictimIdsBySkillCategory(String category) {
        String query = """
                            SELECT DISTINCT vs.victim_id
                            FROM VictimSkill vs
                            JOIN Skill s ON vs.skill_id = s.id
                            JOIN DisasterVictim dv ON vs.victim_id = dv.person_id
                            WHERE s.category = ? AND dv.is_soft_deleted = FALSE
                            ORDER BY vs.victim_id
                        """;

        List<Integer> victimIds = new ArrayList<>();

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    victimIds.add(rs.getInt("victim_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not search victims by skill category.", e);
        }

        return victimIds;
    }
}
