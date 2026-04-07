package edu.ucalgary.oop;

import java.sql.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection dbConnect;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/ensf380project";
    private static final String DB_USER = "oop";
    private static final String DB_PASSWORD = "ucalgary";

    private DatabaseManager() {
        createConnection();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void createConnection() {
        try {
            dbConnect = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Could not connect to database.", e);
        }
    }

    public int insertPerson(String firstName, String lastName, String comments) {
        String query = "INSERT INTO Person (first_name, last_name, comments) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, comments);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert person.", e);
        }

        throw new RuntimeException("Failed to insert person.");
    }

    public void insertDisasterVictim(int personId, java.time.LocalDate dateOfBirth, Integer approximateAge, String gender, java.time.LocalDate entryDate, Integer locationId) {
        String query = "INSERT INTO DisasterVictim (person_id, date_of_birth, approximate_age, gender, entry_date, location_id, is_soft_deleted) VALUES (?, ?, ?, ?, ?, ?, FALSE)";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, personId);

            if (dateOfBirth == null) {
                stmt.setNull(2, java.sql.Types.DATE);
            } else {
                stmt.setDate(2, Date.valueOf(dateOfBirth));
            }

            if (approximateAge == null) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, approximateAge);
            }

            stmt.setString(4, gender);
            stmt.setDate(5, Date.valueOf(entryDate));

            if (locationId == null) {
                stmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(6, locationId);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert disaster victim.", e);
        }
    }

    public void updateVictimName(int personId, String newFirstName, String newLastName) {
        String query = "UPDATE Person SET first_name = ?, last_name = ? WHERE id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setString(1, newFirstName);
            stmt.setString(2, newLastName);
            stmt.setInt(3, personId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No person found with id " + personId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update person name.", e);
        }
    }

    public void updateVictimAgeInfo(int personId, java.time.LocalDate dateOfBirth, Integer approximateAge) {
        String query = "UPDATE DisasterVictim SET date_of_birth = ?, approximate_age = ? WHERE person_id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            if (dateOfBirth == null) {
                stmt.setNull(1, java.sql.Types.DATE);
            } else {
                stmt.setDate(1, Date.valueOf(dateOfBirth));
            }

            if (approximateAge == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, approximateAge);
            }

            stmt.setInt(3, personId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No disaster victim found with id " + personId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update victim age information.", e);
        }
    }

    public void softDeleteVictim(int personId) {
        String query = "UPDATE DisasterVictim SET is_soft_deleted = TRUE WHERE person_id = ?";

        try (PreparedStatement stmt = dbConnect.prepareStatement(query)) {
            stmt.setInt(1, personId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No disaster victim found with id " + personId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to soft delete victim.", e);
        }
    }

    public void hardDeleteVictim(int personId) {
        String deleteInquiry = "DELETE FROM Inquiry WHERE inquirer_id = ? OR subject_person_id = ?";
        String deleteRelationships = "DELETE FROM FamilyRelationship WHERE person_one_id = ? OR person_two_id = ?";
        String deletePerson = "DELETE FROM Person WHERE id = ?";

        try {
            dbConnect.setAutoCommit(false);

            try (PreparedStatement stmt1 = dbConnect.prepareStatement(deleteInquiry);
                 PreparedStatement stmt2 = dbConnect.prepareStatement(deleteRelationships);
                 PreparedStatement stmt3 = dbConnect.prepareStatement(deletePerson)) {

                stmt1.setInt(1, personId);
                stmt1.setInt(2, personId);
                stmt1.executeUpdate();

                stmt2.setInt(1, personId);
                stmt2.setInt(2, personId);
                stmt2.executeUpdate();

                stmt3.setInt(1, personId);
                int rows = stmt3.executeUpdate();

                if (rows == 0) {
                    throw new RuntimeException("No person found with id: " + personId);
                }

                dbConnect.commit();
            } catch (SQLException | RuntimeException e) {
                dbConnect.rollback();
                throw e;
            } finally {
                dbConnect.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to hard delete victim.", e);
        }
    }

    public ResultSet getActiveVictims() {
        String query = "SELECT p.id, p.first_name, p.last_name, p.comments, dv.date_of_birth, dv.approximate_age, dv.gender, dv.entry_date, dv.location_id FROM Person p JOIN DisasterVictim dv ON p.id = dv.person_id WHERE dv.is_soft_deleted = FALSE ORDER BY p.id";

        try {
            PreparedStatement stmt = dbConnect.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get active victims.", e);
        }
    }

    public void close() {
        try {
            if (dbConnect != null && !dbConnect.isClosed()) {
                dbConnect.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close connection.", e);
        }
    }
}