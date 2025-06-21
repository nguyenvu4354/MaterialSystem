/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.DBContext;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

/**
 *
 * @author Nhat Anh
 */
public class RepairRequestDetailDAO extends DBContext {

    public boolean insert(RepairRequestDetail detail) throws SQLException {
        String sql = "INSERT INTO repair_request_details (repair_request_id, material_id, quantity, damage_description, repair_cost, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, detail.getRepairRequestId());
            stmt.setInt(2, detail.getMaterialId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setString(4, detail.getDamageDescription());

            if (detail.getRepairCost() != null) {
                stmt.setDouble(5, detail.getRepairCost());
            } else {
                stmt.setNull(5, java.sql.Types.DOUBLE);
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(6, now); // created_at
            stmt.setTimestamp(7, now); // updated_at

            return stmt.executeUpdate() > 0;
        }
    }

   

    public List<RepairRequestDetail> getRepairRequestDetailsByUserId(int userId) {
        List<RepairRequestDetail> list = new ArrayList<>();
        String query = """
                SELECT rrd.* FROM RepairRequestDetail rrd
                JOIN RepairRequest rr ON rrd.repairRequestId = rr.requestId
                WHERE rr.createdBy = ?;
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail(
                        rs.getInt("detailId"),
                        rs.getInt("repairRequestId"),
                        rs.getInt("materialId"),
                        rs.getInt("quantity"),
                        rs.getString("damageDescription"),
                        rs.getDouble("repairCost"),
                        rs.getTimestamp("createdAt"),
                        rs.getTimestamp("updatedAt")
                );
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
