/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.DBContext;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

;

/**
 *
 * @author Nhat Anh
 */
public class RepairRequestDAO extends DBContext {

    public void insertRepairRequest(RepairRequest req) {
        String sql = "INSERT INTO RepairRequests (request_code, user_id, request_date, repair_person_phone_number, "
                + "repair_person_email, repair_location, estimated_return_date, status, reason, created_at, updated_at, disable) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, req.getRequestCode());
            stmt.setInt(2, req.getUserId());
            stmt.setTimestamp(3, req.getRequestDate());
            stmt.setString(4, req.getRepairPersonPhoneNumber());
            stmt.setString(5, req.getRepairPersonEmail());
            stmt.setString(6, req.getRepairLocation());
            stmt.setDate(7, req.getEstimatedReturnDate());
            stmt.setString(8, req.getStatus());
            stmt.setString(9, req.getReason());
            stmt.setTimestamp(10, req.getCreatedAt());
            stmt.setTimestamp(11, req.getUpdatedAt());
            stmt.setBoolean(12, req.isDisable());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRepairRequest(RepairRequest request, List<RepairRequestDetail> details) throws SQLException {
        String requestSql = "INSERT INTO Repair_Requests (request_code, user_id, repair_person_phone_number, repair_person_email, "
                + "repair_location, estimated_return_date, reason, status, request_date, created_at, updated_at, disable) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String detailSql = "INSERT INTO Repair_Request_Details (repair_request_id, material_id, quantity, damage_description, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                PreparedStatement psRequest = connection.prepareStatement(requestSql, Statement.RETURN_GENERATED_KEYS); PreparedStatement psDetail = connection.prepareStatement(detailSql)) {
            // Insert into Repair_Requests
            psRequest.setString(1, request.getRequestCode());
            psRequest.setInt(2, request.getUserId());
            psRequest.setString(3, request.getRepairPersonPhoneNumber());
            psRequest.setString(4, request.getRepairPersonEmail());
            psRequest.setString(5, request.getRepairLocation());
            psRequest.setDate(6, request.getEstimatedReturnDate());
            psRequest.setString(7, request.getReason());
            psRequest.setString(8, request.getStatus());
            psRequest.setTimestamp(9, request.getRequestDate());
            psRequest.setTimestamp(10, request.getCreatedAt());
            psRequest.setTimestamp(11, request.getUpdatedAt());
            psRequest.setBoolean(12, request.isDisable());

            psRequest.executeUpdate();

            // Lấy ID của Repair Request vừa insert
            ResultSet rs = psRequest.getGeneratedKeys();
            if (rs.next()) {
                int repairRequestId = rs.getInt(1);

                // Insert các chi tiết vào Repair_Request_Details
                for (RepairRequestDetail detail : details) {
                    psDetail.setInt(1, repairRequestId);
                    psDetail.setInt(2, detail.getMaterialId());
                    psDetail.setInt(3, detail.getQuantity());
                    psDetail.setString(4, detail.getDamageDescription());
                    psDetail.setTimestamp(5, detail.getCreatedAt());
                    psDetail.setTimestamp(6, detail.getUpdatedAt());
                    psDetail.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<RepairRequest> getAllRepairRequests() throws SQLException {
        String sql = "SELECT * FROM Repair_Requests WHERE disable = 0";
        List<RepairRequest> requests = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequest request = new RepairRequest();
                request.setRepairRequestId(rs.getInt("repair_request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setUserId(rs.getInt("user_id"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setRepairPersonPhoneNumber(rs.getString("repair_person_phone_number"));
                request.setRepairPersonEmail(rs.getString("repair_person_email"));
                request.setRepairLocation(rs.getString("repair_location"));
                request.setEstimatedReturnDate(rs.getDate("estimated_return_date"));
                request.setStatus(rs.getString("status"));
                request.setReason(rs.getString("reason"));
                request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                request.setApprovalReason(rs.getString("approval_reason"));
                request.setApprovedAt(rs.getTimestamp("approved_at"));
                request.setRejectionReason(rs.getString("rejection_reason"));
                request.setCreatedAt(rs.getTimestamp("created_at"));
                request.setUpdatedAt(rs.getTimestamp("updated_at"));
                request.setDisable(rs.getBoolean("disable"));
                requests.add(request);
            }
        }
        return requests;
    }

    public List<RepairRequestDetail> getRepairRequestDetails(int repairRequestId) throws SQLException {
        List<RepairRequestDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Repair_Request_Details WHERE repair_request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setRepairRequestId(rs.getInt("repair_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setDamageDescription(rs.getString("damage_description"));
                detail.setRepairCost(rs.getObject("repair_cost") != null ? rs.getDouble("repair_cost") : null);
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        }
        return details;
    }

    public List<RepairRequestDetail> getAllRepairRequestDetails() throws SQLException {
        List<RepairRequestDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Repair_Request_Details";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setRepairRequestId(rs.getInt("repair_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setDamageDescription(rs.getString("damage_description"));
                detail.setRepairCost(rs.getObject("repair_cost") != null ? rs.getDouble("repair_cost") : null);
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        }

        return details;
    }

    public List<RepairRequest> getRepairRequestsByUser(int userId) {
        List<RepairRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM RepairRequests WHERE user_id = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequest r = new RepairRequest();
                r.setRepairRequestId(rs.getInt("repair_request_id"));
                r.setRequestCode(rs.getString("request_code"));
                r.setUserId(rs.getInt("user_id"));
                r.setStatus(rs.getString("status"));
                r.setRequestDate(rs.getTimestamp("request_date"));
                r.setReason(rs.getString("reason"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
