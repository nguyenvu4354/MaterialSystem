package dal;

import entity.DBContext;
import entity.Export;
import entity.ExportDetail;
import entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExportDAO extends DBContext {

    private int getNextExportNumber() throws SQLException {
        String sql = "SELECT MAX(export_id) AS max_id FROM Exports";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
            return 1;
        }
    }

    private String generateExportCode(int exportId) throws SQLException {
        return String.format("EXP%06d", exportId);
    }

    public int createExport(Export export) throws SQLException {
        String sql = "INSERT INTO Exports (export_code, export_date, exported_by, recipient_user_id, batch_number, note, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int exportId = getNextExportNumber();
            stmt.setString(1, generateExportCode(exportId));
            stmt.setObject(2, export.getExportDate());
            stmt.setInt(3, export.getExportedBy());
            stmt.setInt(4, export.getRecipientUserId());
            stmt.setString(5, export.getBatchNumber());
            stmt.setString(6, export.getNote());
            stmt.setObject(7, export.getCreatedAt());
            stmt.setObject(8, export.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating export failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating export failed, no ID obtained.");
                }
            }
        }
    }

    public String getExportCode(int exportId) throws SQLException {
        String sql = "SELECT export_code FROM Exports WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("export_code");
                }
            }
        }
        throw new SQLException("Export code not found for export ID: " + exportId);
    }

    public void addMaterialToExport(ExportDetail detail) throws SQLException {
        if (detail.getQuantity() <= 0) {
            throw new SQLException("Quantity must be greater than 0 for material ID: " + detail.getMaterialId());
        }

        String sql = "INSERT INTO Export_Details (export_id, material_id, quantity, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, detail.getExportId());
            stmt.setInt(2, detail.getMaterialId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setString(4, "draft");
            stmt.setObject(5, detail.getCreatedAt());
            stmt.setObject(6, detail.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding material to export failed.");
            }
        }
    }

    public void createExportDetails(List<ExportDetail> details) throws SQLException {
        String insertSql = "INSERT INTO Export_Details (export_id, material_id, quantity, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            for (ExportDetail detail : details) {
                if (detail.getQuantity() <= 0) {
                    throw new SQLException("Quantity must be greater than 0 for material ID: " + detail.getMaterialId());
                }

                ExportDetail existingDetail = getExportDetailByMaterial(detail.getExportId(), detail.getMaterialId());
                if (existingDetail != null) {
                    int newQuantity = existingDetail.getQuantity() + detail.getQuantity();
                    updateExportDetailQuantity(existingDetail.getExportDetailId(), newQuantity);
                } else {
                    insertStmt.setInt(1, detail.getExportId());
                    insertStmt.setInt(2, detail.getMaterialId());
                    insertStmt.setInt(3, detail.getQuantity());
                    insertStmt.setString(4, detail.getStatus());
                    insertStmt.setObject(5, detail.getCreatedAt());
                    insertStmt.setObject(6, detail.getUpdatedAt());
                    insertStmt.addBatch();
                }
            }
            int[] affectedRows = insertStmt.executeBatch();
            for (int rows : affectedRows) {
                if (rows == 0) {
                    if (insertStmt.getUpdateCount() > 0) {
                        throw new SQLException("Adding material to export failed.");
                    }
                }
            }
        }
    }

    public ExportDetail getExportDetailByMaterial(int exportId, int materialId) throws SQLException {
        String sql = "SELECT * FROM Export_Details WHERE export_id = ? AND material_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            stmt.setInt(2, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ExportDetail detail = new ExportDetail();
                    detail.setExportDetailId(rs.getInt("export_detail_id"));
                    detail.setExportId(rs.getInt("export_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    detail.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return detail;
                }
            }
        }
        return null;
    }

    public void updateExportDetailQuantity(int exportDetailId, int newQuantity) throws SQLException {
        String sql = "UPDATE Export_Details SET quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE export_detail_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, exportDetailId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating export detail quantity failed for export detail ID: " + exportDetailId);
            }
        }
    }

    public void confirmExport(int exportId) throws SQLException {
        String sql = "UPDATE Export_Details SET status = 'exported', updated_at = CURRENT_TIMESTAMP WHERE export_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No draft export details found to confirm for export ID: " + exportId);
            }
        }
    }

    public List<ExportDetail> getDraftExportDetails(int exportId) throws SQLException {
        List<ExportDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Export_Details WHERE export_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ExportDetail detail = new ExportDetail();
                    detail.setExportDetailId(rs.getInt("export_detail_id"));
                    detail.setExportId(rs.getInt("export_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    detail.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public void removeExportDetail(int exportId, int materialId, int quantity) throws SQLException {
        String sql = "DELETE FROM Export_Details WHERE export_id = ? AND material_id = ? AND quantity = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            stmt.setInt(2, materialId);
            stmt.setInt(3, quantity);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No draft export detail found to remove for export ID: " + exportId);
            }
        }
    }

    public void updateExportDetailsStatus(int exportId, String status) throws SQLException {
        String sql = "UPDATE Export_Details SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, exportId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No export details updated for export ID: " + exportId);
            }
        }
    }

    public void updateExport(Export export) throws SQLException {
        String sql = "UPDATE Exports SET export_date = ?, exported_by = ?, recipient_user_id = ?, batch_number = ?, note = ?, updated_at = ? WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, export.getExportDate());
            stmt.setInt(2, export.getExportedBy());
            stmt.setInt(3, export.getRecipientUserId());
            stmt.setString(4, export.getBatchNumber());
            stmt.setString(5, export.getNote());
            stmt.setObject(6, export.getUpdatedAt());
            stmt.setInt(7, export.getExportId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No export updated for export ID: " + export.getExportId());
            }
        }
    }

    public void updateInventoryByExportId(int exportId, int updatedBy) throws SQLException {
        InventoryDAO inventoryDAO = new InventoryDAO();
        List<ExportDetail> details = getExportDetailsByExportId(exportId);
        inventoryDAO.updateInventory(details, updatedBy);
    }

    public List<ExportDetail> getExportDetailsByExportId(int exportId) throws SQLException {
        List<ExportDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Export_Details WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ExportDetail detail = new ExportDetail();
                    detail.setExportDetailId(rs.getInt("export_detail_id"));
                    detail.setExportId(rs.getInt("export_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    detail.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public void deleteExport(int exportId) throws SQLException {
        String sql = "DELETE FROM Exports WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No export found to delete for export ID: " + exportId);
            }
        }
    }

    public int getTotalExportedQuantity() throws SQLException {
        String sql = "SELECT SUM(quantity) AS total_exported FROM Export_Details WHERE status = 'exported'";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_exported");
            }
        }
        return 0;
    }

    public List<User> getUsersByRoleIds(List<Integer> roleIds) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.user_id, u.full_name, u.department_id, d.department_name "
                + "FROM Users u "
                + "LEFT JOIN Departments d ON u.department_id = d.department_id "
                + "WHERE u.role_id IN (";

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < roleIds.size(); i++) {
            placeholders.append("?");
            if (i < roleIds.size() - 1) {
                placeholders.append(",");
            }
        }
        query += placeholders.toString() + ") AND u.status = 'active'";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < roleIds.size(); i++) {
                stmt.setInt(i + 1, roleIds.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setDepartmentId(rs.getInt("department_id"));
                    user.setDepartmentName(rs.getString("department_name"));
                    users.add(user);
                }
            }
        }
        return users;
    }
}
