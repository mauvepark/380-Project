package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class SkillRepository {
    private final Connection dbConnect;

    // constructor
    public SkillRepository(Connection dbConnect) {
        this.dbConnect = dbConnect;
    }

    // getters and setters
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

    // delete victim skill
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

    // delete all skills for a victim for hard deletion
    public void deleteAllSkillsForVictim(int victimId) {
        String query = "DELETE FROM VictimSkill WHERE victim_id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, victimId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete victim skills for victim.", e);
        }
    }

    // check if a victim has a specific skill
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