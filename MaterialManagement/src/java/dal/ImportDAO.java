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
        String sql = "INSERT INTO Imports (import_code, import_date, imported_by, supplier_id, destination, batch_number, actual_arrival, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
            if (importData.getActualArrival() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(importData.getActualArrival()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }
            stmt.setString(8, importData.getNote());

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
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    detail.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
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
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating import detail quantity failed for import detail ID: " + importDetailId);
            }
        }
    }

    public void confirmImport(int importId) throws SQLException {
        String sql = "UPDATE Import_Details SET status = 'imported' WHERE import_id = ? AND status = 'draft'";
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
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    detail.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
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
        String sql = "UPDATE Import_Details SET status = ? WHERE import_id = ?";
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
        String sql = "UPDATE Imports SET import_code = ?, import_date = ?, imported_by = ?, supplier_id = ?, destination = ?, batch_number = ?, actual_arrival = ?, note = ? WHERE import_id = ?";
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
            if (importData.getActualArrival() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(importData.getActualArrival()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }
            stmt.setString(8, importData.getNote());
            stmt.setInt(9, importData.getImportId());
            
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
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    detail.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
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
        String sql = "SELECT * FROM Imports WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Import importData = new Import();
                    importData.setImportId(rs.getInt("import_id"));
                    importData.setImportCode(rs.getString("import_code"));
                    importData.setImportDate(rs.getTimestamp("import_date").toLocalDateTime());
                    importData.setImportedBy(rs.getInt("imported_by"));
                    importData.setSupplierId(rs.getInt("supplier_id"));
                    importData.setDestination(rs.getString("destination"));
                    importData.setBatchNumber(rs.getString("batch_number"));
                    if (rs.getTimestamp("actual_arrival") != null) {
                        importData.setActualArrival(rs.getTimestamp("actual_arrival").toLocalDateTime());
                    }
                    importData.setNote(rs.getString("note"));
                    importData.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    importData.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return importData;
                }
            }
        }
        return null;
    }

    public List<Import> getImports(LocalDateTime startDate, LocalDateTime endDate, int pageIndex, int pageSize) throws SQLException {
        List<Import> imports = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Imports WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (startDate != null) {
            sql.append(" AND import_date >= ?");
            params.add(Timestamp.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append(" AND import_date <= ?");
            params.add(Timestamp.valueOf(endDate));
        }

        sql.append(" ORDER BY import_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((pageIndex - 1) * pageSize);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Import importData = new Import();
                    importData.setImportId(rs.getInt("import_id"));
                    importData.setImportCode(rs.getString("import_code"));
                    importData.setImportDate(rs.getTimestamp("import_date").toLocalDateTime());
                    importData.setImportedBy(rs.getInt("imported_by"));
                    importData.setSupplierId(rs.getInt("supplier_id"));
                    importData.setDestination(rs.getString("destination"));
                    importData.setBatchNumber(rs.getString("batch_number"));
                    if (rs.getTimestamp("actual_arrival") != null) {
                        importData.setActualArrival(rs.getTimestamp("actual_arrival").toLocalDateTime());
                    }
                    importData.setNote(rs.getString("note"));
                    importData.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    importData.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    imports.add(importData);
                }
            }
        }
        return imports;
    }

    public int getTotalImportedQuantity() throws SQLException {
        String sql = "SELECT SUM(quantity) AS total_imported FROM Import_Details WHERE status = 'imported'";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_imported");
            }
        }
        return 0;
    }
}