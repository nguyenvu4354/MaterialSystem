package dal;

import entity.DBContext;
import entity.Import;
import entity.ImportDetail;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ImportDAO extends DBContext {

    public int createImport(Import importData) throws SQLException {
        String sql = "INSERT INTO Imports (import_code, import_date, imported_by, supplier_id, destination, batch_number, is_damaged, actual_arrival, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, importData.getImportCode());
            stmt.setObject(2, importData.getImportDate());
            stmt.setInt(3, importData.getImportedBy());
            if (importData.getSupplierId() != null) {
                stmt.setInt(4, importData.getSupplierId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, importData.getDestination());
            stmt.setString(6, importData.getBatchNumber());
            stmt.setBoolean(7, importData.isDamaged());
            if (importData.getActualArrival() != null) {
                stmt.setTimestamp(8, Timestamp.valueOf(importData.getActualArrival()));
            } else {
                stmt.setNull(8, Types.TIMESTAMP);
            }
            stmt.setObject(9, importData.getCreatedAt());
            stmt.setObject(10, importData.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating import failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating import failed, no ID obtained.");
                }
            }
        }
    }

    public void addMaterialToImport(ImportDetail detail) throws SQLException {
        if (detail.getQuantity() <= 0) {
            throw new SQLException("Quantity must be greater than 0 for material ID: " + detail.getMaterialId());
        }

        String sql = "INSERT INTO Import_Details (import_id, material_id, quantity, unit_price, material_condition, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, detail.getImportId());
            stmt.setInt(2, detail.getMaterialId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setDouble(4, detail.getUnitPrice());
            stmt.setString(5, detail.getMaterialCondition());
            stmt.setString(6, "draft");
            stmt.setObject(7, detail.getCreatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding material to import failed.");
            }
        }
    }

    public void createImportDetails(List<ImportDetail> details) throws SQLException {
        String insertSql = "INSERT INTO Import_Details (import_id, material_id, quantity, unit_price, material_condition, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            for (ImportDetail detail : details) {
                if (detail.getQuantity() <= 0) {
                    throw new SQLException("Quantity must be greater than 0 for material ID: " + detail.getMaterialId());
                }

                ImportDetail existingDetail = getImportDetailByMaterialAndCondition(detail.getImportId(), detail.getMaterialId(), detail.getMaterialCondition());
                if (existingDetail != null) {
                    int newQuantity = existingDetail.getQuantity() + detail.getQuantity();
                    updateImportDetailQuantity(existingDetail.getImportDetailId(), newQuantity);
                } else {
                    insertStmt.setInt(1, detail.getImportId());
                    insertStmt.setInt(2, detail.getMaterialId());
                    insertStmt.setInt(3, detail.getQuantity());
                    insertStmt.setDouble(4, detail.getUnitPrice());
                    insertStmt.setString(5, detail.getMaterialCondition());
                    insertStmt.setString(6, detail.getStatus());
                    insertStmt.setObject(7, detail.getCreatedAt());
                    insertStmt.addBatch();
                }
            }
            int[] affectedRows = insertStmt.executeBatch();
            for (int rows : affectedRows) {
                if (rows == 0) {
                    if (insertStmt.getUpdateCount() > 0) {
                        throw new SQLException("Adding material to import failed.");
                    }
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
        String sql = "UPDATE Import_Details SET quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE import_detail_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, importDetailId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating import detail quantity failed for import detail ID: " + importDetailId);
            }
        }
    }

    public void confirmImport(int importId) throws SQLException {
        String sql = "UPDATE Import_Details SET status = 'imported', updated_at = CURRENT_TIMESTAMP WHERE import_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
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
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No draft import detail found to remove for import ID: " + importId);
            }
        }
    }

    public void updateImportDetailsStatus(int importId, String status) throws SQLException {
        String sql = "UPDATE Import_Details SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, importId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No import details updated for import ID: " + importId);
            }
        }
    }

    public void updateImport(Import importData) throws SQLException {
        String sql = "UPDATE Imports SET import_code = ?, import_date = ?, imported_by = ?, supplier_id = ?, destination = ?, batch_number = ?, is_damaged = ?, actual_arrival = ?, updated_at = ? WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, importData.getImportCode());
            stmt.setObject(2, importData.getImportDate());
            stmt.setInt(3, importData.getImportedBy());
            if (importData.getSupplierId() != null) {
                stmt.setInt(4, importData.getSupplierId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, importData.getDestination());
            stmt.setString(6, importData.getBatchNumber());
            stmt.setBoolean(7, importData.isDamaged());
            if (importData.getActualArrival() != null) {
                stmt.setTimestamp(8, Timestamp.valueOf(importData.getActualArrival()));
            } else {
                stmt.setNull(8, Types.TIMESTAMP);
            }
            stmt.setObject(9, importData.getUpdatedAt());
            stmt.setInt(10, importData.getImportId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No import updated for import ID: " + importData.getImportId());
            }
        }
    }

    public void updateInventoryByImportId(int importId, int updatedBy) throws SQLException {
        InventoryDAO inventoryDAO = new InventoryDAO();
        List<ImportDetail> details = getImportDetailsByImportId(importId);
        inventoryDAO.updateInventoryForImport(details, updatedBy);
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
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No import found to delete for import ID: " + importId);
            }
        }
    }

    public Import getImportById(int importId) throws SQLException {
        Import imports = null;
        String sql = "SELECT * FROM Imports WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    imports = new Import();
                    imports.setImportId(rs.getInt("import_id"));
                    imports.setImportCode(rs.getString("import_code"));
                    imports.setImportDate(rs.getTimestamp("import_date").toLocalDateTime());
                    imports.setImportedBy(rs.getInt("imported_by"));
                    int supplierId = rs.getInt("supplier_id");
                    if (!rs.wasNull()) {
                        imports.setSupplierId(supplierId);
                    }
                    imports.setDestination(rs.getString("destination"));
                    imports.setBatchNumber(rs.getString("batch_number"));
                    imports.setIsDamaged(rs.getBoolean("is_damaged"));
                    Timestamp actualArrival = rs.getTimestamp("actual_arrival");
                    if (actualArrival != null) {
                        imports.setActualArrival(actualArrival.toLocalDateTime());
                    }
                    imports.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    imports.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }
        }
        return imports;
    }

    public List<Import> getImports(LocalDateTime startDate, LocalDateTime endDate, int pageIndex, int pageSize) throws SQLException {
        List<Import> list = new ArrayList<>();
        String sql = "SELECT * FROM Imports WHERE import_date BETWEEN ? AND ? ORDER BY import_date DESC LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            stmt.setInt(3, pageSize);
            stmt.setInt(4, (pageIndex - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Import imports = new Import();
                    imports.setImportId(rs.getInt("import_id"));
                    imports.setImportCode(rs.getString("import_code"));
                    imports.setImportDate(rs.getTimestamp("import_date").toLocalDateTime());
                    imports.setImportedBy(rs.getInt("imported_by"));
                    int supplierId = rs.getInt("supplier_id");
                    if (!rs.wasNull()) {
                        imports.setSupplierId(supplierId);
                    }
                    imports.setDestination(rs.getString("destination"));
                    imports.setBatchNumber(rs.getString("batch_number"));
                    imports.setIsDamaged(rs.getBoolean("is_damaged"));
                    Timestamp actualArrival = rs.getTimestamp("actual_arrival");
                    if (actualArrival != null) {
                        imports.setActualArrival(actualArrival.toLocalDateTime());
                    }
                    imports.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    imports.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    list.add(imports);
                }
            }
        }
        return list;
    }
}