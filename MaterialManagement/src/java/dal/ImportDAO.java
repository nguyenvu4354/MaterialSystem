package dal;

import entity.DBContext;
import entity.Import;
import entity.ImportDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ImportDAO extends DBContext {

    public int createImport(Import imports) throws SQLException {
        String sql = "INSERT INTO Imports (import_code, import_date, imported_by, supplier_id, destination, batch_number, is_damaged, actual_arrival, note, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, imports.getImportCode());
            stmt.setObject(2, imports.getImportDate());
            stmt.setInt(3, imports.getImportedBy());
            stmt.setObject(4, imports.getSupplierId());
            stmt.setString(5, imports.getDestination());
            stmt.setString(6, imports.getBatchNumber());
            stmt.setBoolean(7, imports.isDamaged());
            stmt.setObject(8, imports.getActualArrival());
            stmt.setString(9, imports.getNote());
            stmt.setObject(10, imports.getCreatedAt());
            stmt.setObject(11, imports.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating import failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Creating import failed, no ID obtained.");
            }
        }
    }

    public void createImportDetails(List<ImportDetail> details) throws SQLException {
        String sql = "INSERT INTO Import_Details (import_id, material_id, quantity, unit_price, expiry_date, material_condition, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (ImportDetail detail : details) {
                if (detail.getQuantity() <= 0) {
                    throw new SQLException("Quantity must be greater than 0 for material ID: " + detail.getMaterialId());
                }

                ImportDetail existingDetail = getImportDetailByMaterialAndCondition(detail.getImportId(), detail.getMaterialId(), detail.getMaterialCondition());
                if (existingDetail != null) {
                    int newQuantity = existingDetail.getQuantity() + detail.getQuantity();
                    updateImportDetailQuantity(existingDetail.getImportDetailId(), newQuantity);
                } else {
                    stmt.setInt(1, detail.getImportId());
                    stmt.setInt(2, detail.getMaterialId());
                    stmt.setInt(3, detail.getQuantity());
                    stmt.setDouble(4, detail.getUnitPrice());
                    stmt.setObject(5, detail.getExpiryDate());
                    stmt.setString(6, detail.getMaterialCondition());
                    stmt.setString(7, detail.getStatus());
                    stmt.setObject(8, detail.getCreatedAt());
                    stmt.addBatch();
                }
            }
            int[] affectedRows = stmt.executeBatch();
            for (int rows : affectedRows) {
                if (rows == 0) {
                    throw new SQLException("Adding material to import failed.");
                }
            }
        }
    }

    public ImportDetail getImportDetailByMaterialAndCondition(int importId, int materialId, String materialCondition) throws SQLException {
        String sql = "SELECT * FROM Import_Details WHERE import_id = ? AND material_id = ? AND material_condition = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            stmt.setInt(2, materialId);
            stmt.setString(3, materialCondition);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportDetailId(rs.getInt("import_detail_id"));
                    detail.setImportId(rs.getInt("import_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setExpiryDate(rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null);
                    detail.setMaterialCondition(rs.getString("material_condition"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return detail;
                }
            }
        }
        return null;
    }

    public void updateImportDetailQuantity(int importDetailId, int newQuantity) throws SQLException {
        String sql = "UPDATE Import_Details SET quantity = ? WHERE import_detail_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, importDetailId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Updating import detail quantity failed for import detail ID: " + importDetailId);
            }
        }
    }

    public void confirmImport(int importId) throws SQLException {
        String sql = "UPDATE Import_Details SET status = 'exported' WHERE import_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No draft import details found to confirm for import ID: " + importId);
            }
        }
    }

    public List<ImportDetail> getDraftImportDetails(int importId) throws SQLException {
        List<ImportDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Import_Details WHERE import_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportDetailId(rs.getInt("import_detail_id"));
                    detail.setImportId(rs.getInt("import_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setExpiryDate(rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null);
                    detail.setMaterialCondition(rs.getString("material_condition"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public void removeImportDetail(int importId, int materialId, int quantity, String materialCondition) throws SQLException {
        String sql = "DELETE FROM Import_Details WHERE import_id = ? AND material_id = ? AND quantity = ? AND material_condition = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            stmt.setInt(2, materialId);
            stmt.setInt(3, quantity);
            stmt.setString(4, materialCondition);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No draft import detail found to remove for import ID: " + importId);
            }
        }
    }

    public void updateImport(Import imports) throws SQLException {
        String sql = "UPDATE Imports SET import_code = ?, import_date = ?, imported_by = ?, supplier_id = ?, destination = ?, batch_number = ?, is_damaged = ?, actual_arrival = ?, note = ?, updated_at = ? WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, imports.getImportCode());
            stmt.setObject(2, imports.getImportDate());
            stmt.setInt(3, imports.getImportedBy());
            stmt.setObject(4, imports.getSupplierId());
            stmt.setString(5, imports.getDestination());
            stmt.setString(6, imports.getBatchNumber());
            stmt.setBoolean(7, imports.isDamaged());
            stmt.setObject(8, imports.getActualArrival());
            stmt.setString(9, imports.getNote());
            stmt.setObject(10, imports.getUpdatedAt());
            stmt.setInt(11, imports.getImportId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No import updated for import ID: " + imports.getImportId());
            }
        }
    }

    public void updateInventoryByImportId(int importId, int updatedBy) throws SQLException {
        List<ImportDetail> details = getImportDetailsByImportId(importId);
        new InventoryDAO().updateInventoryForImport(details, updatedBy);
    }

    public List<ImportDetail> getImportDetailsByImportId(int importId) throws SQLException {
        List<ImportDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Import_Details WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportDetailId(rs.getInt("import_detail_id"));
                    detail.setImportId(rs.getInt("import_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setExpiryDate(rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toLocalDate() : null);
                    detail.setMaterialCondition(rs.getString("material_condition"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public void deleteImport(int importId) throws SQLException {
        String sql = "DELETE FROM Imports WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No import found to delete for import ID: " + importId);
            }
        }
    }
}