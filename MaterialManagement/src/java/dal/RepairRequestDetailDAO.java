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

    public List<RepairRequestDetail> getAllRepairRequestDetails() {
        List<RepairRequestDetail> list = new ArrayList<>();
        String query = "SELECT * FROM Repair_Request_Details";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail(
                        rs.getInt("detail_id"),
                        rs.getInt("repair_request_id"),
                        rs.getInt("material_id"),
                        rs.getInt("quantity"),
                        rs.getString("damage_description"),
                        rs.getDouble("repair_cost"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<RepairRequestDetail> getRepairRequestDetailsByUserId(int userId) {
        List<RepairRequestDetail> list = new ArrayList<>();
        String sql = """
        SELECT 
            rrd.detail_id,
            rrd.repair_request_id,
            rrd.material_id,
            rrd.quantity,
            rrd.damage_description,
            rrd.repair_cost,
            rrd.created_at AS detail_created_at,
            rrd.updated_at AS detail_updated_at,
            
            rr.request_code,
            rr.request_date,
            rr.status,
            rr.approved_by,
            rr.approved_at,
            rr.rejection_reason
            
        FROM Repair_Request_Details rrd
        JOIN Repair_Requests rr ON rrd.repair_request_id = rr.repair_request_id
        WHERE rr.user_id = ?
        ORDER BY rr.request_date DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail();

                // Thông tin chi tiết vật tư cần sửa
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setRepairRequestId(rs.getInt("repair_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setDamageDescription(rs.getString("damage_description"));
                detail.setRepairCost(rs.getDouble("repair_cost"));
                detail.setCreatedAt(rs.getTimestamp("detail_created_at"));
                detail.setUpdatedAt(rs.getTimestamp("detail_updated_at"));

//                 Gợi ý mở rộng: Nếu bạn có DTO, bạn có thể set thêm:
//                 detail.setStatus(rs.getString("status"));
//                 detail.setRequestCode(rs.getString("request_code"));
//                 detail.setApprovedAt(rs.getTimestamp("approved_at"));
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<RepairRequestDetail> getRepairRequestDetailsByRoleId(int roleId) {
        List<RepairRequestDetail> list = new ArrayList<>();
        String sql = """
        SELECT 
            rrd.detail_id,
            rrd.repair_request_id,
            rrd.material_id,
            rrd.quantity,
            rrd.damage_description,
            rrd.repair_cost,
            rrd.created_at AS detail_created_at,
            rrd.updated_at AS detail_updated_at,

            rr.request_code,
            rr.request_date,
            rr.status,
            rr.approved_by,
            rr.approved_at,
            rr.rejection_reason

        FROM Repair_Request_Details rrd
        JOIN Repair_Requests rr ON rrd.repair_request_id = rr.repair_request_id
        JOIN Users u ON rr.user_id = u.user_id
        WHERE u.role_id = ?
        ORDER BY rr.request_date DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail();

                detail.setDetailId(rs.getInt("detail_id"));
                detail.setRepairRequestId(rs.getInt("repair_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setDamageDescription(rs.getString("damage_description"));
                detail.setRepairCost(rs.getDouble("repair_cost"));
                detail.setCreatedAt(rs.getTimestamp("detail_created_at"));
                detail.setUpdatedAt(rs.getTimestamp("detail_updated_at"));

                // Nếu cần, bạn có thể set thêm thông tin từ bảng Repair_Requests vào detail (hoặc tạo DTO)
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<RepairRequestDetail> getRepairRequestDetailsByRequestId(int repairRequestId) {
        List<RepairRequestDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM Repair_Request_Details WHERE repair_request_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail(
                        rs.getInt("detail_id"),
                        rs.getInt("repair_request_id"),
                        rs.getInt("material_id"),
                        rs.getInt("quantity"),
                        rs.getString("damage_description"),
                        rs.getDouble("repair_cost"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void main(String[] args) {
        RepairRequestDetailDAO dao = new RepairRequestDetailDAO();
        List<RepairRequestDetail> list = dao.getRepairRequestDetailsByRequestId(1); // ví dụ: role_id = 3 là "Thủ kho"
        for (RepairRequestDetail d : list) {
            System.out.println(d.toString());
        }
    }
}
