package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SupplyService {
    private final DatabaseManager databaseManager;
    private final ActionLogger logger;

    public SupplyService() {
        this.databaseManager = DatabaseManager.getInstance();
        this.logger = ActionLogger.getInstance();
    }

    public SupplyService(DatabaseManager databaseManager, ActionLogger logger) {
        if (databaseManager == null || logger == null) {
            throw new IllegalArgumentException("Dependencies cannot be null.");
        }
        this.databaseManager = databaseManager;
        this.logger = logger;
    }

    // loads supplies from the database
    public List<Supply> getAllSupplies() {
        String sql = "SELECT id, supply_type, location_id, victim_id, expiry_date, allocation_date, description FROM Supply ORDER BY id";

        List<Supply> supplies = new ArrayList<>();

        try (
            Connection connection = databaseManager.getConnection();
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

    // loads supplies from the database by locaiton
    public List<Supply> getSuppliesByLocation(int locationId) {
        String sql = """SELECT id, supply_type, location_id, victim_id, expiry_date, allocation_date, description 
                        FROM Supply 
                        WHERE location_id = ? 
                        ORDER BY id""";

        List<Supply> supplies = new ArrayList<>();

        try (
            Connection connection = databaseManager.getConnection();
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

    // loads supplies from database non-expired non-allocated
    public List<Supply> getAvailableInventoryForAllocation(int locationId) {
        String sql = """SELECT id, supply_type, location_id, victim_id, expiry_date, allocation_date, description 
                        FROM Supply 
                        WHERE location_id = ? AND victim_id IS NULL AND (expiry_date IS NULL OR expiry_date >= CURRENT_DATE) 
                        ORDER BY supply_type, id""";

        List<Supply> supplies = new ArrayList<>();

        try (
            Connection connection = databaseManager.getConnection();
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

    // loads expired supplies by location
    public List<Supply> getExpiredSuppliesByLocation(int locationId) {
        String sql = """SELECT id, supply_type, location_id, victim_id, expiry_date, allocation_date, 
                        description FROM Supply WHERE location_id = ?
                        AND expiry_date IS NOT NULL
                        AND expiry_date < CURRENT_DATE
                        ORDER BY expiry_date, id""";

        List<Supply> supplies = new ArrayList<>();

        try (
            Connection connection = databaseManager.getConnection();
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

    // adds a new supply to the database
    public Supply addSupply(Supply supply) {
        validateSupply(supply);

        String sql = "INSERT INTO Supply (supply_type, location_id, victim_id, expiry_date, allocation_date, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = databaseManager.getConnection();
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

    // updates supply
    public void updateSupply(Supply supply) {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null.");
        }
        if (supply.getId() <= 0) {
            throw new IllegalArgumentException("Supply must have a valid ID.");
        }

        validateSupply(supply);

        String sql = """UPDATE Supply
                        SET supply_type = ?, location_id = ?, victim_id = ?, expiry_date = ?, allocation_date = ?, description = ?
                        WHERE id = ?""";

        try (
            Connection connection = databaseManager.getConnection();
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

    // allocates supply to a victim
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

        try (
            Connection connection = databaseManager.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, victimId);
            stmt.setInt(2, supplyId);

            logger.log("UPDATED", "supply " + supplyId + " | Type: " + supply.getSupplyType() + " -> allocated to disaster victim " + victimId);

        } catch (SQLException e) {
            throw new RuntimeException("Could not allocate supply.", e);
        }
    }

    // unallocates supply from victim
    public void unallocateSupply(int supplyId) {
        Supply supply = getSupplyById(supplyId);

        if (supply == null) {
            throw new IllegalArgumentException("Supply not found.");
        }

        String sql = "UPDATE Supply SET victim_id = NULL, allocation_date = NULL WHERE id = ?";

        try (
            Connection connection = databaseManager.getConnection();
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

    // deletes supply
    public void deleteSupply(int supplyId) {
        Supply supply = getSupplyById(supplyId);
        if (supply == null) {
            throw new IllegalArgumentException("Supply not found.");
        }

        String sql = "DELETE FROM Supply WHERE id = ?";

        try (
            Connection connection = databaseManager.getConnection();
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

    // gets supply by ID
    public Supply getSupplyById(int supplyId) {
        String sql = """
            SELECT id, supply_type, location_id, victim_id, expiry_date, allocation_date, description
            FROM Supply
            WHERE id = ?
            """;

        try (
            Connection connection = databaseManager.getConnection();
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

    // validation check
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

    // fills ? in prepared statements
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

    // maps result to supply object
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

    // gets nullable integer from result set
    private Integer getNullInt(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);

        if (rs.wasNull()) {
            return null;
        }
        return value;
    }
}