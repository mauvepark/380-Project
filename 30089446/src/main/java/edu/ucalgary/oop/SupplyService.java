package edu.ucalgary.oop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing supplies.
 * This class loads, adds, updates, allocates, unallocates, and deletes supply records.
 */
public class SupplyService {
    private final DatabaseManager databaseManager;
    private final ActionLogger logger;

    /**
     * Constructor for SupplyService.
     * Uses the shared database manager and action logger.
     */
    public SupplyService() {
        this.databaseManager = DatabaseManager.getInstance();
        this.logger = ActionLogger.getInstance();
    }

    /**
     * Constructor for SupplyService with dependency injection.
     *
     * @param databaseManager the database manager used for connections
     * @param logger the logger used to record actions
     */
    public SupplyService(DatabaseManager databaseManager, ActionLogger logger) {
        this.databaseManager = databaseManager;
        this.logger = logger;
    }

    /**
     * Gets all supplies from the database.
     *
     * @return a list of all supplies
     * @throws RuntimeException if the supplies cannot be loaded
     */
    public List<Supply> getAllSupplies() {
        String sql = """
                        SELECT s.id, s.supply_type, s.location_id, s.victim_id, s.expiry_date, s.allocation_date, s.description
                        FROM Supply s
                        LEFT JOIN DisasterVictim dv ON s.victim_id = dv.person_id
                        WHERE s.victim_id IS NULL OR dv.is_soft_deleted = FALSE
                        ORDER BY s.id
                     """;
        List<Supply> supplies = new ArrayList<>();

        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                supplies.add(mapRowToSupply(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load supplies.", e);
        }

        return supplies;
    }

    /**
     * Gets all supplies for a specific location.
     *
     * @param locationId the location ID to search for
     * @return a list of supplies at that location
     * @throws RuntimeException if the supplies cannot be loaded
     */
    public List<Supply> getSuppliesByLocation(int locationId) {
        String sql = """
                        SELECT s.id, s.supply_type, s.location_id, s.victim_id, s.expiry_date, s.allocation_date, s.description
                        FROM Supply s
                        LEFT JOIN DisasterVictim dv ON s.victim_id = dv.person_id
                        WHERE s.location_id = ?
                        AND (s.victim_id IS NULL OR dv.is_soft_deleted = FALSE)
                        ORDER BY s.id
                    """;

        List<Supply> supplies = new ArrayList<>();


        Connection connection = databaseManager.getConnection();
        
        try (
            
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, locationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    supplies.add(mapRowToSupply(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load supplies by location.", e);
        }

        return supplies;
    }

    /**
     * Gets supplies that are available for allocation at a location.
     * Only non-expired and unallocated supplies are returned.
     *
     * @param locationId the location ID to search for
     * @return a list of allocatable supplies
     * @throws RuntimeException if the inventory cannot be loaded
     */
    public List<Supply> getAvailableInventoryForAllocation(int locationId) {
        String sql = """
                        SELECT id, supply_type, location_id, victim_id, expiry_date, allocation_date, description 
                        FROM Supply 
                        WHERE location_id = ? AND victim_id IS NULL AND (expiry_date IS NULL OR expiry_date >= CURRENT_DATE) 
                        ORDER BY supply_type, id
                    """;

        List<Supply> supplies = new ArrayList<>();

        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, locationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    supplies.add(mapRowToSupply(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load allocatable inventory.", e);
        }

        return supplies;
    }

    /**
     * Gets expired supplies for a specific location.
     *
     * @param locationId the location ID to search for
     * @return a list of expired supplies
     * @throws RuntimeException if the expired supplies cannot be loaded
     */
    public List<Supply> getExpiredSuppliesByLocation(int locationId) {
        String sql = """
                        SELECT s.id, s.supply_type, s.location_id, s.victim_id, s.expiry_date, s.allocation_date, s.description
                        FROM Supply s
                        LEFT JOIN DisasterVictim dv ON s.victim_id = dv.person_id
                        WHERE s.location_id = ?
                        AND s.expiry_date IS NOT NULL
                        AND s.expiry_date < CURRENT_DATE
                        AND (s.victim_id IS NULL OR dv.is_soft_deleted = FALSE)
                        ORDER BY s.expiry_date, s.id
                    """;

        List<Supply> supplies = new ArrayList<>();

        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, locationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    supplies.add(mapRowToSupply(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load supplies.", e);
        }

        return supplies;
    }

    /**
     * Adds a new supply to the database.
     *
     * @param supply the supply to add
     * @return the saved supply with its generated ID
     * @throws IllegalArgumentException if the supply data is invalid
     * @throws RuntimeException if the supply cannot be added
     */
    public Supply addSupply(Supply supply) {
        validateSupply(supply);

        String sql = "INSERT INTO Supply (supply_type, location_id, victim_id, expiry_date, allocation_date, description) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            fillSupplyFields(stmt, supply);
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("Could not insert supply.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    supply.setId(keys.getInt(1));
                } else {
                    throw new RuntimeException("Supply inserted but no ID returned.");
                }
            }

            logger.log("ADDED", "supply " + supply.getId() + " | Type: " + supply.getSupplyType() + ", Location ID: " + supply.getLocationId());

            return supply;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add supply.", e);
        }
    }

    /**
     * Updates an existing supply.
     *
     * @param supply the supply to update
     * @throws IllegalArgumentException if the supply is null, invalid, or has no valid ID
     * @throws RuntimeException if the supply cannot be updated
     */
    public void updateSupply(Supply supply) {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null.");
        }
        if (supply.getId() <= 0) {
            throw new IllegalArgumentException("Supply must have a valid ID.");
        }

        validateSupply(supply);

        String sql = """
                        UPDATE Supply
                        SET supply_type = ?, location_id = ?, victim_id = ?, expiry_date = ?, allocation_date = ?, description = ?
                        WHERE id = ?
                    """;
        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            fillSupplyFields(stmt, supply);
            stmt.setInt(7, supply.getId());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No supply found with ID " + supply.getId());
            }

            logger.log("UPDATED", "supply " + supply.getId() + " | Type: " + supply.getSupplyType() + ", Victim ID: " + supply.getVictimId() + ", Expiry: " + supply.getExpiryDate());

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update supply.", e);
        }
    }

    /**
     * Allocates a supply to a victim.
     *
     * @param supplyId the supply ID
     * @param victimId the victim ID
     * @throws IllegalArgumentException if the supply does not exist
     * @throws IllegalStateException if the supply is already allocated or expired
     * @throws RuntimeException if the supply cannot be allocated
     */
    public void allocateSupply(int supplyId, int victimId) {
        Supply supply = getSupplyById(supplyId);
        if (supply == null) {
            throw new IllegalArgumentException("Supply not found.");
        }

        if (supply.isAllocated()) {
            throw new IllegalStateException("Supply is already allocated.");
        }

        if (supply.isExpired()) {
            throw new IllegalStateException("Expired supply cannot be allocated.");
        }

        String sql = "UPDATE Supply SET victim_id = ?, allocation_date = CURRENT_DATE WHERE id = ?";

        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, victimId);
            stmt.setInt(2, supplyId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Supply allocation failed.");
            }

            logger.log("UPDATED", "supply " + supplyId + " | Type: " + supply.getSupplyType() + " -> allocated to disaster victim " + victimId);

        } catch (SQLException e) {
            throw new RuntimeException("Could not allocate supply.", e);
        }
    }

    /**
     * Removes a supply allocation from a victim.
     *
     * @param supplyId the supply ID
     * @throws IllegalArgumentException if the supply does not exist
     * @throws RuntimeException if the supply cannot be unallocated
     */
    public void unallocateSupply(int supplyId) {
        Supply supply = getSupplyById(supplyId);

        if (supply == null) {
            throw new IllegalArgumentException("Supply not found.");
        }

        String sql = "UPDATE Supply SET victim_id = NULL, allocation_date = NULL WHERE id = ?";

        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, supplyId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Supply unallocation failed.");
            }

            logger.log("UPDATED", "supply " + supplyId + " | Type: " + supply.getSupplyType() + " -> returned to inventory");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to unallocate supply.", e);
        }
    }

    /**
     * Deletes a supply from the database.
     *
     * @param supplyId the supply ID
     * @throws IllegalArgumentException if the supply does not exist
     * @throws RuntimeException if the supply cannot be deleted
     */
    public void deleteSupply(int supplyId) {
        Supply supply = getSupplyById(supplyId);
        if (supply == null) {
            throw new IllegalArgumentException("Supply not found.");
        }

        String sql = "DELETE FROM Supply WHERE id = ?";

        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, supplyId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No supply deleted.");
            }

            logger.log("DELETED", "supply " + supplyId + " | Type: " + supply.getSupplyType());

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete supply.", e);
        }
    }

    /**
     * Gets a supply by its ID.
     *
     * @param supplyId the supply ID
     * @return the matching supply, or null if not found
     * @throws RuntimeException if the supply cannot be loaded
     */
    public Supply getSupplyById(int supplyId) {
        String sql = """
                        SELECT id, supply_type, location_id, victim_id, expiry_date, allocation_date, description
                        FROM Supply
                        WHERE id = ?
                    """;

        Connection connection = databaseManager.getConnection();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, supplyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSupply(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load supply.", e);
        }

        return null;
    }

    /**
     * Validates a supply before saving it.
     *
     * @param supply the supply to validate
     * @throws IllegalArgumentException if the supply data is invalid
     */
    private void validateSupply(Supply supply) {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null.");
        }
        if (supply.getSupplyType() == null || supply.getSupplyType().trim().isEmpty()) {
            throw new IllegalArgumentException("Supply type is required.");
        }
        if (supply.getLocationId() == null) {
            throw new IllegalArgumentException("Location is required.");
        }
        if (supply.getAllocationDate() != null && supply.getVictimId() == null) {
            throw new IllegalArgumentException("Allocated date cannot be set without a victim.");
        }
        if (supply.getVictimId() != null && supply.isExpired()) {
            throw new IllegalArgumentException("Expired supply cannot be assigned to a victim.");
        }
    }

    /**
     * Fills a prepared statement with supply values.
     *
     * @param stmt the prepared statement to fill
     * @param supply the supply providing the values
     * @throws SQLException if a database field cannot be set
     */
    private void fillSupplyFields(PreparedStatement stmt, Supply supply) throws SQLException {
        stmt.setString(1, supply.getSupplyType());

        if (supply.getLocationId() == null) {
            stmt.setNull(2, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(2, supply.getLocationId());
        }

        if (supply.getVictimId() == null) {
            stmt.setNull(3, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(3, supply.getVictimId());
        }

        if (supply.getExpiryDate() == null) {
            stmt.setNull(4, java.sql.Types.DATE);
        } else {
            stmt.setDate(4, Date.valueOf(supply.getExpiryDate()));
        }

        if (supply.getAllocationDate() == null) {
            stmt.setNull(5, java.sql.Types.DATE);
        } else {
            stmt.setDate(5, Date.valueOf(supply.getAllocationDate()));
        }

        stmt.setString(6, supply.getDescription());
    }

    /**
     * Converts a database row into a Supply object.
     *
     * @param rs the result set row
     * @return the mapped Supply object
     * @throws SQLException if the row data cannot be read
     */
    private Supply mapRowToSupply(ResultSet rs) throws SQLException {
        Date expirySqlDate = rs.getDate("expiry_date");
        Date allocationSqlDate = rs.getDate("allocation_date");

        return new Supply(
            rs.getInt("id"),
            rs.getString("supply_type"),
            getNullInt(rs, "location_id"),
            getNullInt(rs, "victim_id"),
            expirySqlDate == null ? null : expirySqlDate.toLocalDate(),
            allocationSqlDate == null ? null : allocationSqlDate.toLocalDate(),
            rs.getString("description")
        );
    }

    /**
     * Reads an integer column that may be null.
     *
     * @param rs the result set
     * @param columnName the column name to read
     * @return the integer value, or null if the column is null
     * @throws SQLException if the column cannot be read
     */
    private Integer getNullInt(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);

        if (rs.wasNull()) {
            return null;
        }
        return value;
    }
}
