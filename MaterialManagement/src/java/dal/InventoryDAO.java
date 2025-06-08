package dal;

import entity.DBContext;
import entity.ExportDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class InventoryDAO extends DBContext {

    // Existing updateInventory method
    public void updateInventory(List<ExportDetail> details, int updatedBy) throws SQLException {
        Objects.requireNonNull(details, "Danh sách chi tiết xuất kho không được null");
        if (details.isEmpty()) {
            throw new SQLException("Danh sách chi tiết xuất kho không được rỗng.");
        }
        if (updatedBy <= 0) {
            throw new SQLException("ID người dùng cập nhật không hợp lệ: " + updatedBy);
        }

        String checkStockSql = "SELECT stock FROM Inventory WHERE material_id = ?";
        String updateStockSql = "UPDATE Inventory SET stock = stock - ?, last_updated = CURRENT_TIMESTAMP, updated_by = ? WHERE material_id = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkStockSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateStockSql)) {
            for (ExportDetail detail : details) {
                checkStmt.setInt(1, detail.getMaterialId());
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy bản ghi tồn kho cho vật tư ID: " + detail.getMaterialId());
                }
                int currentStock = rs.getInt("stock");
                if (currentStock < detail.getQuantity()) {
                    throw new SQLException("Tồn kho không đủ cho vật tư ID: " + detail.getMaterialId() + ". Tồn kho hiện tại: " + currentStock + ", Yêu cầu: " + detail.getQuantity());
                }

                updateStmt.setInt(1, detail.getQuantity());
                updateStmt.setInt(2, updatedBy);
                updateStmt.setInt(3, detail.getMaterialId());
                int affectedRows = updateStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Cập nhật tồn kho thất bại cho vật tư ID: " + detail.getMaterialId());
                }
            }
        }
    }

    // New method to get stock quantity for a material
    public int getStockByMaterialId(int materialId) throws SQLException {
        String sql = "SELECT stock FROM Inventory WHERE material_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                } else {
                    return 0; // Return 0 if no inventory record exists
                }
            }
        }
    }
}