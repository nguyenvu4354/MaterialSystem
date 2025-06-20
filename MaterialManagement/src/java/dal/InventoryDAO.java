package dal;

import entity.DBContext;
import entity.ExportDetail;
import entity.ImportDetail;
import entity.Inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryDAO extends DBContext {

    public void updateInventory(List<ExportDetail> details, int updatedBy) throws SQLException {
        Objects.requireNonNull(details, "Export details list cannot be null");
        if (details.isEmpty()) {
            throw new SQLException("Export details list cannot be empty.");
        }
        if (updatedBy <= 0) {
            throw new SQLException("Invalid user ID for update: " + updatedBy);
        }

        String checkStockSql = "SELECT stock FROM Inventory WHERE material_id = ?";
        String updateStockSql = "UPDATE Inventory SET stock = stock - ?, updated_by = ?, last_updated = CURRENT_TIMESTAMP WHERE material_id = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkStockSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateStockSql)) {
            for (ExportDetail detail : details) {
                checkStmt.setInt(1, detail.getMaterialId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int currentStock = rs.getInt("stock");
                        if (currentStock < detail.getQuantity()) {
                            throw new SQLException("Insufficient stock for material ID: " + detail.getMaterialId() +
                                    ". Available: " + currentStock + ", Required: " + detail.getQuantity());
                        }
                        updateStmt.setInt(1, detail.getQuantity());
                        updateStmt.setInt(2, updatedBy);
                        updateStmt.setInt(3, detail.getMaterialId());
                        if (updateStmt.executeUpdate() == 0) {
                            throw new SQLException("Failed to update inventory for material ID: " + detail.getMaterialId());
                        }
                    } else {
                        throw new SQLException("Inventory information not found for material ID: " + detail.getMaterialId());
                    }
                }
            }
        }
    }

    public void updateInventoryForImport(List<ImportDetail> details, int updatedBy) throws SQLException {
        Objects.requireNonNull(details, "Import details list cannot be null");
        if (details.isEmpty()) {
            throw new SQLException("Import details list cannot be empty.");
        }
        if (updatedBy <= 0) {
            throw new SQLException("Invalid user ID for update: " + updatedBy);
        }

        String checkStockSql = "SELECT stock FROM Inventory WHERE material_id = ?";
        String updateStockSql = "UPDATE Inventory SET stock = stock + ?, updated_by = ?, last_updated = CURRENT_TIMESTAMP WHERE material_id = ?";
        String insertStockSql = "INSERT INTO Inventory (material_id, stock, updated_by, last_updated, location) VALUES (?, ?, ?, CURRENT_TIMESTAMP, 'Default Warehouse')";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkStockSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateStockSql);
             PreparedStatement insertStmt = connection.prepareStatement(insertStockSql)) {
            for (ImportDetail detail : details) {
                checkStmt.setInt(1, detail.getMaterialId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        updateStmt.setInt(1, detail.getQuantity());
                        updateStmt.setInt(2, updatedBy);
                        updateStmt.setInt(3, detail.getMaterialId());
                        if (updateStmt.executeUpdate() == 0) {
                            throw new SQLException("Failed to update inventory for material ID: " + detail.getMaterialId());
                        }
                    } else {
                        insertStmt.setInt(1, detail.getMaterialId());
                        insertStmt.setInt(2, detail.getQuantity());
                        insertStmt.setInt(3, updatedBy);
                        if (insertStmt.executeUpdate() == 0) {
                            throw new SQLException("Failed to create new inventory for material ID: " + detail.getMaterialId());
                        }
                    }
                }
            }
        }
    }

    public int getStockByMaterialId(int materialId) throws SQLException {
        String sql = "SELECT stock FROM Inventory WHERE material_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        }
        return 0;
    }

    public List<Inventory> getAllInventory() throws SQLException {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT i.*, m.material_name, m.material_code, c.category_name, u.unit_name " +
                    "FROM Inventory i " +
                    "JOIN Materials m ON i.material_id = m.material_id " +
                    "LEFT JOIN Categories c ON m.category_id = c.category_id " +
                    "LEFT JOIN Units u ON m.unit_id = u.unit_id " +
                    "WHERE m.disable = 0 " +
                    "ORDER BY m.material_code";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Inventory inventory = new Inventory();
                inventory.setInventoryId(rs.getInt("inventory_id"));
                inventory.setMaterialId(rs.getInt("material_id"));
                inventory.setStock(rs.getInt("stock"));
                inventory.setLocation(rs.getString("location"));
                inventory.setNote(rs.getString("note"));
                inventory.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
                inventory.setUpdatedBy(rs.getInt("updated_by"));
                
                // Additional material information
                inventory.setMaterialName(rs.getString("material_name"));
                inventory.setMaterialCode(rs.getString("material_code"));
                inventory.setCategoryName(rs.getString("category_name"));
                inventory.setUnitName(rs.getString("unit_name"));
                
                inventoryList.add(inventory);
            }
        }
        return inventoryList;
    }
}